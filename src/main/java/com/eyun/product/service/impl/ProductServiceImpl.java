package com.eyun.product.service.impl;

import com.eyun.product.domain.AttrValue;
import com.eyun.product.domain.Attribute;
import com.eyun.product.domain.ProductImg;
import com.eyun.product.repository.*;
import com.eyun.product.service.ProductService;
import com.eyun.product.domain.Product;
import com.eyun.product.service.dto.ProductContentDTO;
import com.eyun.product.service.dto.ProductDTO;
import com.eyun.product.service.dto.ProductSeachParam;
import com.eyun.product.service.mapper.ProductMapper;
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
    public Map publishProductAndSku(ProductContentDTO productContentDTO) {
        String message="success";
        String content="发布商品成功";
        Product product=productRepository.findProductByName(productContentDTO.getProductName());
        if (product==null){
            product=new Product();
            product.setName(productContentDTO.getProductName());
            product.setShopId(productContentDTO.getShopId());
            product.setBrandId(productContentDTO.getBrandId());
            product.setListPrice(productContentDTO.getListPrice());
            product.setDetails(productContentDTO.getDescription());
            product.setDeleted(false);
            product.setCreatedTime(Instant.now());
            product=productRepository.save(product);//product
            ProductImg productImg=new ProductImg();
            productImg .setImgUrl(productContentDTO.getMainImage());
            productImg.setProductId(product.getId());
            productImg.setCreatedTime(Instant.now());
            productImg.setDeleted(false);
            productImgRepository.save(productImg);//productImage
        }
        List<Map<String,String>>attrList=productContentDTO.getAttr();
        for (Map<String,String> attr:attrList){
            Iterator<Map.Entry<String,String>> iterator=attr.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,String>entry=iterator.next();
                String key=entry.getKey();
                String value=entry.getValue();
                Attribute attribute=attributeRepository.findAttributeByProductIdAndNameAndDeleted(product.getId(),key,0);
                if (attribute==null){
                    attribute=new Attribute();
                    attribute.setProductId(product.getId());
                    attribute.setName(key);
                    attribute.setCreatedTime(Instant.now());
                    attribute.setDeleted(0);
                    attribute=attributeRepository.save(attribute);//attr
                }
                AttrValue attrValue=new AttrValue();
                attrValue.setAttrId(attribute.getId());
                attrValue.setValue(value);
                attrValue.setCreatedTime(Instant.now());
                attrValue.deleted(false);
                attrValue=attrValueRepository.save(attrValue);//attrValue

            }
        }
        Map result=new HashMap();
        result.put("message",message);
        result.put("content",content);
        return result;
    }

    @Override
    public Map findProductById(Long id){
        Map result=new HashMap();
        Map product=productRepository.findProductById(id);
        result.put("productContent",product);
        List attrbuteList=new ArrayList();
        List<Map>attrList=productRepository.findProductAttrById(id);
        if (!attrList.isEmpty()&&attrList.size()>0){
            for (Map attr:attrList){
                 Map value=new HashMap();
                 value.put("attname",attr.get("attname"));
                 Object object=attr.get("attvalue");
                 if (object!=null){
                     List list=new ArrayList();
                     if (object.toString().indexOf(",")>0){
                         String[]array=object.toString().split(",");
                         for (String param:array){
                             list.add(param);
                         }
                     }else {
                         list.add(object);
                     }
                     value.put("attvalue",list);
                 }
                 attrbuteList.add(value);
            }
        }
        result.put("attrbute",attrbuteList);
        return result;
    }
    /*商品列表*/
    @Override
    public Map findProductByCatewgory(ProductSeachParam productSeachParam) {
        String sql="";
        StringBuffer fromSku=new StringBuffer("LEFT JOIN product_sku sku ON p.id=sku.product_id ");
        StringBuffer addWhere=new StringBuffer("WHERE ca.id = :categoryid AND p.deleted = 0 ");
        StringBuffer groupBy=new StringBuffer(" GROUP BY p.id");
        StringBuffer select=new StringBuffer("SELECT p.id AS id, p. NAME AS NAME, p.list_price AS listPrice, ( SELECT IFNULL(img.img_url,\"\") FROM product_img img WHERE img.jhi_type = 1 AND img.product_id = p.id AND img.deleted=0 limit 0,1) AS url FROM product p  LEFT JOIN brand b ON b.id = p.brand_id LEFT JOIN category ca ON ca.id = b.category_id ");
        if (StringUtils.isBlank(productSeachParam.getProductName())&&StringUtils.isBlank(productSeachParam.getSale())&&StringUtils.isBlank(productSeachParam.getPrice())&&productSeachParam.getStartPrice()==null&&productSeachParam.getEndPrice()==null){
            sql=select.append(addWhere).toString();
        }
        if (StringUtils.isNotBlank(productSeachParam.getProductName())){
            sql=select.append(addWhere).append(" and p. NAME like \"%"+productSeachParam.getProductName()+"%\"").toString();
        }
        if (StringUtils.isNotBlank(productSeachParam.getSale())){
            sql=select.append(fromSku).append(addWhere).append(groupBy).append(" ORDER BY sku.count").toString();
        }
        if (StringUtils.isNotBlank(productSeachParam.getPrice())){
            sql=select.append(addWhere).append(" ORDER BY p.list_price").toString();
        }
        if (productSeachParam.getStartPrice()!=null&&productSeachParam.getEndPrice()!=null){
            sql=select.append(addWhere).append(" AND p.list_price BETWEEN "+productSeachParam.getStartPrice()+" AND "+productSeachParam.getEndPrice()).append(groupBy).toString();
        }
        Query query=entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setParameter("categoryid",productSeachParam.getCategoryId());
        List<Map> list=query.getResultList();
        Map result=new HashMap();
        result.put("mainContent",list);
        return result;
    }

    @Override
    public List<Map> findProductByIds(List<Long> ids) {
        List<Map> productList =productRepository.findProductByIds(ids);
        return productList;
    }

    @Override
    public List<Product> findProductByShopIdAndDeleted(Long shopId, Boolean deleted) {
        List<Product> productList =productRepository.findProductsByShopIdAndDeleted(shopId,deleted);
        return productList;
    }

    @Override
    public List initSku(List<Map<String, List<String>>> productAttr) throws Exception{
        Map<String,List<String>>elemet=productAttr.remove(0);
        Iterator<Map.Entry<String,List<String>>>iterator=elemet.entrySet().iterator();
        Map.Entry<String,List<String>>entry=iterator.next();
        String attr=entry.getKey();
        List<String>valueList=entry.getValue();
        List<Map> attrList=new ArrayList();
        for(String value:valueList){
            if (productAttr.size()==1){
                Map<String,List<String>>otherElemet=productAttr.get(0);
                Iterator<Map.Entry<String,List<String>>>ite=otherElemet.entrySet().iterator();
                while (ite.hasNext()){
                    Map.Entry<String,List<String>>otherEntry=ite.next();
                    String attrOther=otherEntry.getKey();
                    List<String>valueListOther=otherEntry.getValue();
                    for (String otherValue:valueListOther){
                        Map map=new HashMap();
                        map.put("attr",value);
                        map.put("attrAnother",otherValue);
                        attrList.add(map);
                    }
                }
            }
        }
        return attrList;
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
   /* public static void main(String[]args)throws Exception{
        List<Map<String,List<String>>> list=new ArrayList();
        List list1=new ArrayList();
        list1.add("白色");
        list1.add("黑色");
        list1.add("灰色");
        list1.add("蓝色");
        list1.add("金色");
        Map<String,List<String>> map1=new HashMap();
        map1.put("颜色",list1);
        list.add(map1);
        List list2=new ArrayList();
        list2.add("64G");
        list2.add("128G");
        list2.add("256G");
        Map<String,List<String>> map2=new HashMap();
        map2.put("内存",list2);
        list.add(map2);
        List list3=new ArrayList();
        list3.add("公开版");
        list3.add("双网通版");
        list3.add("分期版");
        Map<String,List<String>> map3=new HashMap();
        map3.put("版本",list3);
        list.add(map3);
        for (Map<String,List<String>> map:list){
            Iterator<Map.Entry<String,List<String>>>iterator=map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<String>> entry = iterator.next();
                String attr = entry.getKey();
                List<String> valueList = entry.getValue();
                for (String value : valueList) {
                    System.out.println(attr + ":" + value);
                }
            }
        }
    *//********************************************************************************************//*


       for (JSONObject json:attrList){
            Iterator<String> ite=json.keys();
            while (ite.hasNext()){
                String key=ite.next();
                System.out.println(key+":"+json.get(key));
            }
        }
    }*/
}
