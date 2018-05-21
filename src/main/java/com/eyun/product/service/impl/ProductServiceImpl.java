package com.eyun.product.service.impl;

import com.eyun.product.domain.*;
import com.eyun.product.repository.*;
import com.eyun.product.service.ProductService;
import com.eyun.product.service.dto.ProductContentDTO;
import com.eyun.product.service.dto.ProductDTO;
import com.eyun.product.service.dto.ProductSeachParam;
import com.eyun.product.service.mapper.ProductMapper;

import com.eyun.product.web.rest.errors.BadRequestAlertException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;


/**
 * Service Implementation for managing Product.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Autowired
    AttributeRepository attributeRepository;

    @Autowired
    AttrValueRepository attrValueRepository;

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductImgRepository productImgRepository;

    @Autowired
    ProductSkuRepository productSkuRepository;

    @Autowired
    SkuImgRepository skuImgRepository;

    @PersistenceContext
    EntityManager entityManager;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Save a product.
     *
     * @param productDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ProductDTO save(ProductDTO productDTO) {
        log.debug("Request to save Product : {}", productDTO);
        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    public List<Map> publishProductAndSku(ProductContentDTO productContentDTO) throws Exception {
        List result = new ArrayList();
        Product product = productRepository.findProductByNameAndShopId(productContentDTO.getProductName(), productContentDTO.getShopId());
        if (product == null) {
            product = new Product();
            product.setName(productContentDTO.getProductName());
            product.setShopId(productContentDTO.getShopId());
            //product.setBrandId(productContentDTO.getBrandId());
            product.setCategoryId(productContentDTO.getCategoryId());
            product.setListPrice(productContentDTO.getListPrice());
            product.setDetails(productContentDTO.getDescription());
            product.setDeleted(false);
            product.setCreatedTime(Instant.now());
            product = productRepository.save(product);//product
        } else {
            product.setCategoryId(productContentDTO.getCategoryId());
            product.setListPrice(productContentDTO.getListPrice());
            product.setDetails(productContentDTO.getDescription());
            product.setDeleted(false);
            product.setUpdatedTime(Instant.now());
            product = productRepository.save(product);//product
        }

        ProductImg productImg = productImgRepository.findByProductId(product.getId());
        if (productImg == null) {
            productImg = new ProductImg();
            productImg.setImgUrl(productContentDTO.getMainImage());
            productImg.setProductId(product.getId());
            productImg.setCreatedTime(Instant.now());
            productImg.setDeleted(false);
            productImgRepository.save(productImg);//productImage
        } else {
            productImg.setImgUrl(productContentDTO.getMainImage());
            productImg.setUpdatedTime(Instant.now());
            productImg.setDeleted(false);
            productImgRepository.save(productImg);//productImage
        }

        List<Map<String, String>> attrList = productContentDTO.getAttr();
        for (Map<String, String> attr : attrList) {
            String attrName = attr.get("attr");
            String value = attr.get("attrValue");
            if (!checkParam(attrName) || !checkParam(value)) {
                throw new BadRequestAlertException("商品属性为空！", "attr", "attrNotfound");
            }
            String attrAnother = attr.get("attrAnother");
            String AnotherValue = attr.get("AnotherValue");
            StringBuffer skuName = new StringBuffer(value);
            Attribute attribute = attributeRepository.findAttributeByProductIdAndNameAndDeleted(product.getId(), attrName, 0);
            if (attribute == null) {
                attribute = new Attribute();
                attribute.setProductId(product.getId());
                attribute.setName(attrName);
                attribute.status(0);
                attribute.setCreatedTime(Instant.now());
                attribute.setDeleted(0);
                attribute = attributeRepository.save(attribute);//attr
            }

            AttrValue attrValue = attrValueRepository.findAllByAttrIdAndValue(attribute.getId(), value);
            if (attrValue == null) {
                attrValue = new AttrValue();
                attrValue.setAttrId(attribute.getId());
                attrValue.setValue(value);
                attrValue.setCreatedTime(Instant.now());
                attrValue.deleted(false);
                attrValue = attrValueRepository.save(attrValue);//attrValue
            }
            StringBuffer attrString = new StringBuffer().append(attrValue.getId());

            if (StringUtils.isNotBlank(attrAnother) && StringUtils.isNotBlank(AnotherValue)) {
                skuName.append(" ").append(AnotherValue);
                Attribute attributeAnother = attributeRepository.findAttributeByProductIdAndNameAndDeleted(product.getId(), attrAnother, 0);
                if (attributeAnother == null) {
                    attributeAnother = new Attribute();
                    attributeAnother.setProductId(product.getId());
                    attributeAnother.setName(attrAnother);
                    attributeAnother.status(0);
                    attributeAnother.setCreatedTime(Instant.now());
                    attributeAnother.setDeleted(0);
                    attributeAnother = attributeRepository.save(attributeAnother);//attr
                }

                AttrValue attrValueAnother = attrValueRepository.findAllByAttrIdAndValue(attributeAnother.getId(), AnotherValue);
                if (attrValueAnother == null) {
                    attrValueAnother = new AttrValue();
                    attrValueAnother.setAttrId(attributeAnother.getId());
                    attrValueAnother.setValue(AnotherValue);
                    attrValueAnother.setCreatedTime(Instant.now());
                    attrValueAnother.deleted(false);
                    attrValueAnother = attrValueRepository.save(attrValueAnother);//attrValue
                }
                attrString.append(",").append(attrValueAnother.getId());
            }

            String skuPrice = attr.get("skuPrice");
            if (!checkParam(skuPrice)) {
                throw new BadRequestAlertException("商品单价为空！", "skuPrice", "skuPriceNotfound");
            }
            String transfer = attr.get("transfer");//让利
            if (!checkParam(transfer)) {
                transfer = "10";
            }
            if (transfer.indexOf("%") > 0) {
                transfer = transfer.substring(0, transfer.indexOf("%"));
            }
            String skuCount = attr.get("skuCount");
            String skuCode = attr.get("skuCode");
            if (!checkParam(skuCode)) {
                throw new BadRequestAlertException("商品编号为空！", "skuCode", "skuCodeNotfound");
            }
            ProductSku sku = productSkuRepository.findByAttrString(attrString.toString());
            Double fer = Double.valueOf(transfer);
            if (fer < 10) {
                throw new BadRequestAlertException("服务费百分比不能小于12", "ServiceCharge", "serviceChargeDosentrRequest");
            } else if (fer > 96) {
                throw new BadRequestAlertException("服务费百分比不能大于98", "ServiceCharge", "serviceChargeDosentrRequest");
            }
            DecimalFormat df = new DecimalFormat("0.00");
            BigDecimal decimal = new BigDecimal(df.format((double) fer / 100));
            if (sku == null) {
                sku = new ProductSku();
                sku.setProductId(product.getId());
                sku.skuName(productContentDTO.getProductName() + " " + skuName);
                sku.setPrice(new BigDecimal(skuPrice));
                sku.setCount(Integer.valueOf(skuCount));
                sku.setAttrString(attrString.toString());
                sku.setSkuCode(skuCode);
                sku.setTransfer(decimal);
                sku.setCreatedTime(Instant.now());
                sku.status(0);//上架
                sku.setDeleted(false);
                sku = productSkuRepository.save(sku);//sku
            } else {
                sku.setSkuCode(skuCode);
                sku.setTransfer(decimal);
                sku.setPrice(new BigDecimal(skuPrice));
                sku.setCount(sku.getCount()+Integer.valueOf(skuCount));
                sku.setUpdatedTime(Instant.now());
                sku.status(0);//上架
                sku.setDeleted(false);
                sku = productSkuRepository.save(sku);
            }
            Map skuMap = new HashMap();
            skuMap.put("skuId", sku.getId());
            skuMap.put("skuName", productContentDTO.getProductName() + " " + skuName);
            result.add(skuMap);
        }
        return result;
    }

    @Override
    public Map findProductById(Long id) {
        Map result = new HashMap();
        Map product = productRepository.findProductById(id);
        String[] urlArray = new String[]{};
        if (product.get("url") != null) {
            urlArray = product.get("url").toString().split(",");
        }
        String integral = "";
        if (product.get("price") != null && product.get("transfer") != null) {
            Double price = Double.valueOf(product.get("price").toString());
            Double transfer = Double.valueOf(product.get("transfer").toString());
            integral = String.valueOf(price * transfer * 10);
        }
        product.put("integral", integral);
        product.put("url", urlArray);
        result.put("productContent", product);
        List attrbuteList = new ArrayList();
        List<Map> attrList = productRepository.findProductAttrById(id);
        if (!attrList.isEmpty() && attrList.size() > 0) {
            for (Map attr : attrList) {
                Map value = new HashMap();
                value.put("attname", attr.get("attname"));
                String attvalue = attr.get("attrvalue").toString();
                if (StringUtils.isNotBlank(attvalue)) {
                    List list = new ArrayList();
                    if (attvalue.indexOf(",") > 0) {
                        String[] array = attvalue.split(",");
                        for (String param : array) {
                            Map map = new HashMap();
                            map.put("valueId", param.substring(0, param.indexOf(":")));
                            map.put("value", param.substring(param.indexOf(":") + 1, param.length()));
                            list.add(map);
                        }
                    } else {
                        Map map = new HashMap();
                        map.put("valueId", attvalue.substring(0, attvalue.indexOf(":")));
                        map.put("value", attvalue.substring(attvalue.indexOf(":") + 1, attvalue.length()));
                        list.add(map);
                    }
                    value.put("attvalue", list);
                }
                attrbuteList.add(value);
            }
        }
        result.put("attrbute", attrbuteList);
        return result;
    }

    /*商品列表*/
    @Override
    public Map findProductByCatewgory(ProductSeachParam productSeachParam) {
        String sql = "";
        StringBuffer fromSku = new StringBuffer("LEFT JOIN product_sku sku ON p.id=sku.product_id ");
        StringBuffer addWhere = new StringBuffer("WHERE ca.id = :categoryid AND p.deleted = 0 AND sku.count>0");
        StringBuffer groupBy = new StringBuffer(" GROUP BY p.id");
        StringBuffer select = new StringBuffer("SELECT DISTINCT(p.id) AS id, p. NAME AS NAME, p.list_price AS listPrice, img.img_url AS url FROM product p LEFT JOIN product_img img ON p.id = img.product_id LEFT JOIN category ca ON ca.id = p.category_id ");
        StringBuffer limit = new StringBuffer(" LIMIT :start,:size");
        if (StringUtils.isBlank(productSeachParam.getProductName()) && StringUtils.isBlank(productSeachParam.getSale()) && StringUtils.isBlank(productSeachParam.getPrice()) && productSeachParam.getStartPrice() == null && productSeachParam.getEndPrice() == null) {
            sql = select.append(fromSku).append(addWhere).toString();
        }
        if (StringUtils.isNotBlank(productSeachParam.getProductName())) {
            sql = select.append(addWhere).append(" and p. NAME like \"%" + productSeachParam.getProductName() + "%\"").toString();
        }
        if (StringUtils.isNotBlank(productSeachParam.getSale())) {
            sql = select.append(fromSku).append(addWhere).append(groupBy).append(" ORDER BY sku.count").toString();
        }
        if (StringUtils.isNotBlank(productSeachParam.getPrice())) {
            sql = select.append(addWhere).append(" ORDER BY p.list_price").toString();
        }
        if (productSeachParam.getStartPrice() != null && productSeachParam.getEndPrice() != null) {
            sql = select.append(addWhere).append(" AND p.list_price BETWEEN " + productSeachParam.getStartPrice() + " AND " + productSeachParam.getEndPrice()).append(groupBy).toString();
        }
        Integer num = productSeachParam.getPageNum();
        Integer size = productSeachParam.getPageSize();
        Integer start = (num - 1) * size;
        Query query = entityManager.createNativeQuery(sql + limit);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setParameter("categoryid", productSeachParam.getCategoryId());
        query.setParameter("start", start);
        query.setParameter("size", size);
        List<Map> list = query.getResultList();
        Map result = new HashMap();
        result.put("mainContent", list);
        return result;
    }

    @Override
    public List<Map> findProductByIds(List<Long> ids) {
        List<Map> productList = productRepository.findProductByIds(ids);
        return productList;
    }

    @Override
    public List<Map> findProductByShopIdAndDeleted(Long shopId, Integer pageNum, Integer pageSize) {
        Integer start = (pageNum - 1) * pageSize;
        List<Map> productList = productRepository.findProductsByShopIdAndDeleted(shopId, start, pageSize);
        return productList;
    }

    @Override
    public List initSku(List<Map> productAttr) throws Exception {
        Map elemet = productAttr.remove(0);
        List valueArray = (List) elemet.get("attrValue");
        List<Map> attrList = new ArrayList();
        for (int i = 0; i < valueArray.size(); i++) {
            if (productAttr.size() == 1) {
                Map elemet1 = productAttr.get(0);
                List valueArrayAnother = (List) elemet1.get("attrValue");
                for (int j = 0; j < valueArrayAnother.size(); j++) {
                    Map map = new HashMap();
                    map.put("attr", valueArray.get(i));
                    map.put("attrAnother", valueArrayAnother.get(j));
                    attrList.add(map);
                }
            }
        }
        return attrList;
    }

    @Override
    public Map skuListStore(ProductSeachParam productSeachParam) throws Exception {
        Long shopId = productSeachParam.getShopId();
        Integer num = productSeachParam.getPageNum();
        Integer size = productSeachParam.getPageSize();
        Integer start = (num - 1) * size;
        String total = "SELECT count(sku.id) AS count FROM product p, product_sku sku WHERE p.id = sku.product_id AND p.shop_id = :shopId";
        Query query = entityManager.createNativeQuery(total);
        query.setParameter("shopId", shopId);
        Integer totalCount = Integer.valueOf(query.getSingleResult().toString());

        String sqlLimit = "SELECT sku.id AS skuId, sku.sku_name AS skuName, sku.count AS count, sku.price AS price, ifnull(sku.status,\"\") AS STATUS, sku.transfer AS transfer, sku.sku_code AS skuCode, IFNULL(sku.created_time, \"\") AS publishTime FROM product p, product_sku sku WHERE p.id = sku.product_id AND p.shop_id = :shopId LIMIT :start,:size";
        Query querySku = entityManager.createNativeQuery(sqlLimit);
        querySku.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        querySku.setParameter("shopId", shopId);
        querySku.setParameter("start", start);
        querySku.setParameter("size", size);
        List<Map> result = querySku.getResultList();
        Integer pageCount = 0;
        if (totalCount > 0) {//计算总页数
            if (totalCount % size > 0) {
                pageCount = totalCount / size + 1;
            } else {
                pageCount = totalCount / size;

            }
        }
        Map sku = new HashMap();
        sku.put("totalCount", totalCount);
        sku.put("skuList", result);
        sku.put("pageCount", pageCount);
        return sku;
    }

    @Override
    public String upLoadskuImage(List<Map> skuImages) throws Exception {
        for (Map sku : skuImages) {
            Long skuId = Long.valueOf(sku.get("skuId").toString());
            if (skuId == null) {
                throw new BadRequestAlertException("skuId为空", "skuId", "skuIdNotexit");
            }
            List<String> skuImageList = (List) (sku.get("skuImage"));
            if (skuImageList.isEmpty()) {
                throw new BadRequestAlertException("skuImage为空", "skuImage", "skuImageNotexit");
            }
            List<SkuImg> list = skuImgRepository.findAllBySkuId(skuId);
            if (list.isEmpty()) {
                for (String image : skuImageList) {
                    SkuImg skuImg = new SkuImg();
                    skuImg.setSkuId(skuId);
                    skuImg.setImgUrl(image);
                    skuImg.setType(1);//图片
                    skuImg.setCreatedTime(Instant.now());
                    skuImg.deleted(false);
                    skuImgRepository.save(skuImg);
                }
            } else {
                Integer count = 0;
                lable1:
                for (String img : skuImageList) {
                    if (list.size() > 0) {
                        for (SkuImg image : list) {
                            image.setImgUrl(img);
                            image.setDeleted(false);
                            image.setUpdatedTime(Instant.now());
                            skuImgRepository.save(image);
                            list.remove(image);
                            count++;
                            if (count == list.size()) {
                                break;
                            }
                            continue lable1;
                        }
                    }
                    Integer left = skuImageList.size() - count;
                    if (left > 0) {
                        SkuImg image = new SkuImg();
                        image.setSkuId(skuId);
                        image.setImgUrl(img);
                        image.setType(1);
                        image.setCreatedTime(Instant.now());
                        image.setDeleted(false);
                        skuImgRepository.save(image);
                    }
                }

            }
        }
        return "success";
    }

    @Override
    public List<Map> findProductByParam(ProductSeachParam productSeachParam) {
        List<Map> result = productRepository.findProductByParam(productSeachParam.getProductName());
        return result;
    }

    /**
     * Get all the products.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Products");
        return productRepository.findAll(pageable)
            .map(productMapper::toDto);
    }

    /**
     * Get one product by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ProductDTO findOne(Long id) {
        log.debug("Request to get Product : {}", id);
        Product product = productRepository.findOne(id);
        return productMapper.toDto(product);
    }

    public Boolean checkParam(String param) {
        if (StringUtils.isNotBlank(param)) {
            return true;
        }
        return false;
    }

    /**
     * Delete the product by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Product : {}", id);
        productRepository.delete(id);
    }

    /*public static void main(String[] args) throws Exception {

        List<Map<String, Object>> list = new ArrayList();
        Map<String, Object> map1 = new HashMap();
        map1.put("attr", "颜色");
        List list1 = new ArrayList();
        list1.add("白色");
        list1.add("黑色");
        list1.add("灰色");
        list1.add("蓝色");
        list1.add("金色");
        map1.put("attrValue", list1);
        list.add(map1);
        Map<String, Object> map2 = new HashMap();
        map2.put("attr", "内存");
        List list2 = new ArrayList();
        list2.add("64G");
        list2.add("128G");
        list2.add("256G");
        map2.put("attrValue", list2);
        list.add(map2);
        Map<String, Object> map3 = new HashMap();
        map3.put("attr", "版本");
        List list3 = new ArrayList();
        list3.add("公开版");
        list3.add("双网通版");
        list3.add("分期版");
        map3.put("attrValue", list3);
        list.add(map3);
        System.out.println("**********************************************initSku**************************************************");


        Integer count = 0;
        List<Map<String, String>> resultList = new ArrayList<>();
        while (list.size() > 0) {
            count++;
            Map<String, Object> element = list.remove(0);
            String attr = element.get("attr").toString();
            List<String> valueList = (List<String>) element.get("attrValue");
            for (String value : valueList) {
                Map<String, String> result = new IdentityHashMap<>();
                result.put(attr, value);
                //resultList.add(result);
                if (count>1){
                    for (Map<String, String> eloter:resultList){

                    }
                }
            }
        }



        System.out.println(resultList.size());
        for (Map<String, String> element1 : resultList) {
            System.out.println("========================================");
            Iterator<Map.Entry<String, String>> iterator = element1.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> param = iterator.next();
                System.out.println(param.getKey() + ":" + param.getValue());
            }

        }
    }*/

}
