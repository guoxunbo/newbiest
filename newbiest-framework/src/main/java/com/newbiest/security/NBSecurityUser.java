package com.newbiest.security;

import com.google.common.collect.Lists;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.main.NewbiestConfiguration;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.model.NBUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 将用户和Security下的检查结合
 * Created by guoxunbo on 2017/11/18.
 */
@Data
@NoArgsConstructor
public class NBSecurityUser implements UserDetails {

    private NBUser nbUser;

    public NBSecurityUser(NBUser nbUser) {
        this.nbUser = nbUser;
    }

    /**
     * 取得用户所拥有的roles。在检查资源权限的时候进行Check
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = Lists.newArrayList();

        List<NBRole> roles = nbUser.getRoles();
        if (CollectionUtils.isNotEmpty(roles)) {
            for (NBRole role : roles) {
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.getRoleId());
                grantedAuthorities.add(simpleGrantedAuthority);
            }
        }

        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return nbUser.getPassword();
    }

    @Override
    public String getUsername() {
        return nbUser.getUsername();
    }

    /**
     * 是否密码过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        Date loginDate = new Date();
        return nbUser.getPwdExpiry().after(loginDate);
    }

    /**
     * 是否被锁住 如密码次数过多
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return nbUser.getPwdWrongCount() < NewbiestConfiguration.getPwdWrongCount();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return nbUser.getActiveFlag();
    }
}
