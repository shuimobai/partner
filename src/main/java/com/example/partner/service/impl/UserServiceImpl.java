package com.example.partner.service.impl;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.partner.common.Constants;
import com.example.partner.controller.domain.UserRequest;
import com.example.partner.entity.User;
import com.example.partner.exception.ServiceException;
import com.example.partner.mapper.UserMapper;
import com.example.partner.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.partner.utils.EmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.partner.common.Constants.USER_NAME_PREFIX;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 程序员人走茶凉
 * @since 2022-12-27
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    //key是code value是当前时间戳
    private  static  final Map<String,Long> CODE_MAP=new ConcurrentHashMap<>();
    private static final long TIME_IN_MS5=5* 60*1000;//表示5分钟的毫米数
    @Autowired
    EmailUtils emailUtils;


    @Override
    public User login(User user) {
        User dbUser=null;
     try{
          dbUser= getOne(new UpdateWrapper<User>().eq("username",user.getUsername()));
     }catch (Exception e){
         throw  new RuntimeException("系统异常");
       }
       if(dbUser == null){
           throw  new ServiceException("未找到用户");
       }
       if(!user.getPassword().equals(dbUser.getPassword())){
           throw new ServiceException("密码错误");
       }
        return dbUser;
    }



    @Override
    public User register(UserRequest user) {
       //校验邮箱
        validateEmail(user.getEmailCode());
        try {
            User saveUser = new User();
            BeanUtils.copyProperties(user,saveUser);  //把请求的数据copy给储存数据库的属性
            //储存数据
            return saveUser(saveUser);
        } catch (Exception e) {
            throw new ServiceException("数据库异常",e);
        }

    }

    @Override
    public void sendEmail(String email, String type) {
        String code = RandomUtil.randomNumbers(6);
        log.info("本次验证的code是：{}", code);
        String context = "<b>尊敬的用户：</b><br><br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您好，" +
                "汉口学院提醒您本次的验证码是：<b>{}</b>，" +
                "有效期5分钟。<br><br><br><b>hkxy</b>";
        String html = StrUtil.format(context, code);
        if ("REGISTER".equals(type)) {
            //校验邮箱是否注册
           User user= getOne(new QueryWrapper<User>().eq("email",email));
           if(user !=null){
               throw  new ServiceException("邮箱已注册过");
           }
            ThreadUtil.execAsync(() -> {  //多线程执行异步请求，可以防止网络阻塞
                emailUtils.sendHtml("【ikun】邮箱注册验证", html, email);
            });
            CODE_MAP.put(code, System.currentTimeMillis());
        }
    }

    /**
     * 密码重置
     * @param userRequest
     * @return
     */
    @Override
    public String passwordReset(UserRequest userRequest) {
        //校验邮箱
        String email = userRequest.getEmail();
        User dbUser = getOne(new UpdateWrapper<User>().eq("email", email));
        if(dbUser==null){
            throw new ServiceException("未找到用户");
        }
        //校验邮箱验证码
        validateEmail(userRequest.getEmailCode());
        //重置新密码
        String newPass="123456";
        dbUser.setPassword(newPass);
        return newPass;
    }


    private User saveUser(User user){
        User dbUser = getOne(new UpdateWrapper<User>().eq("username", user.getUsername()));
        if (dbUser != null) {
            throw new ServiceException("用户名已注册");
        }
        //设置昵称
        if (StrUtil.isBlank(user.getName())) {
            String name = USER_NAME_PREFIX + DateUtil.format(new Date(), Constants.DATE_RULE_YYYYMMDD) + RandomUtil.randomString(4);
            user.setName(name);
        }
        //判断注册用户密码
        if(StrUtil.isBlank(user.getPassword())){
            user.setPassword("123");  //设置默认密码
        }
        //设置用户唯一标识
        user.setUid(IdUtil.fastSimpleUUID());

        boolean saveSuccess = save(user);
        if (!saveSuccess) {
            throw new RuntimeException("注册失败");
        }

        return user;
    }

    /**
     * 校验邮箱
     * @param emailCode
     */
    private void validateEmail(String emailCode){
        //校验邮箱
        Long timestamp = CODE_MAP.get(emailCode);
        if(timestamp==null){
            throw new ServiceException("请先验证邮箱");
        }
        if(timestamp+TIME_IN_MS5 <System.currentTimeMillis()){
            throw new ServiceException("验证码过期");
        }
        //清除缓存
        CODE_MAP.remove(emailCode);
    }
}
