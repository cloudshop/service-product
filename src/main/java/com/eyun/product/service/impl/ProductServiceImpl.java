package com.eyun.product.service.impl;

import com.eyun.product.repository.CategoryRepository;
import com.eyun.product.service.ProductService;
import com.eyun.product.domain.Product;
import com.eyun.product.repository.ProductRepository;
import com.eyun.product.service.dto.ProductDTO;
import com.eyun.product.service.dto.ProductSeachParam;
import com.eyun.product.service.mapper.ProductMapper;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    CategoryRepository categoryRepository;

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
        StringBuffer select=new StringBuffer("SELECT p.id AS id, p. NAME AS NAME, p.list_price AS listPrice, ( SELECT IFNULL(img.img_url,\"\") FROM product_img img WHERE img.jhi_type = 1 AND img.product_id = p.id AND img.deleted=0 ) AS url FROM product p  LEFT JOIN brand b ON b.id = p.brand_id LEFT JOIN category ca ON ca.id = b.category_id ");
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
    public List<Map<String,String>> findProductByIds(List<Long> ids) {
        String sql="SELECT p.id id, p. NAME AS productName, p.list_price AS Price, b.brand_name AS brandName, IFNULL(GROUP_CONCAT(av.jhi_value),\"\") AS attrValue FROM product p LEFT JOIN brand b ON p.brand_id = b.id LEFT JOIN attribute att ON att.product_id = p.id LEFT JOIN attr_value av ON att.id = av.attr_id WHERE p.id IN (?1) GROUP BY p.id";
        Query query=entityManager.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setParameter("1",ids);
        List<Map<String,String>> productList=query.getResultList();
        return productList;
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
}
