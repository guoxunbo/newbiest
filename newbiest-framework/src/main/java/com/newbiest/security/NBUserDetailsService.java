//package com.newbiest.security;
//
//import com.newbiest.security.model.NBUser;
//import com.newbiest.security.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//
///**
// *
// * Created by guoxunbo on 2017/11/18.
// */
//@Component
//public class NBUserDetailsService implements UserDetailsService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        NBUser nbUser = userRepository.getDeepUser(username, false);
//        if (nbUser == null) {
//            throw new UsernameNotFoundException("username " + username + " not found");
//        }
//        NBSecurityUser nbSecurityUser = new NBSecurityUser(nbUser);
//        return nbSecurityUser;
//    }
//
//}
