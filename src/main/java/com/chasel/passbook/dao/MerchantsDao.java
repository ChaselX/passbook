package com.chasel.passbook.dao;

import com.chasel.passbook.entity.Merchants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * <h1>Merchants Dao 接口</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/6 15:36
 */
public interface MerchantsDao extends JpaRepository<Merchants, Integer> {

    /**
     * <h2>通过 id 获取商户对象</h2>
     *
     * @param id 商户 id
     * @return {@link Merchants}
     */
    Optional<Merchants> findById(Integer id);

    /**
     * <h2>根据商户名称获取商户对象</h2>
     *
     * @param name 商户名称
     * @return {@link Merchants}
     */
    Merchants findByName(String name);

    /**
     * <h2>根据商户 ids 获取商户对象</h2>
     *
     * @param ids 商户 ids
     * @return {@link Merchants}
     */
    List<Merchants> findByIdIn(List<Integer> ids);
}
