package com.baidu.shop.aspect;

import com.baidu.shop.comment.MyLog;
import com.baidu.shop.config.GetIp;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.LogEntity;
import com.baidu.shop.mapper.LogMapper;
import com.baidu.shop.util.CookieUtils;
import com.baidu.shop.util.JwtUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @ClassName MyLogAspect
 * @Description: 切面处理类
 * @Author lite
 * @Date 2020/10/27
 * @Version V1.0
 **/
@Aspect
@Component
public class MyLogAspect {

    @Resource
    private JwtConfig jwtConfig;

    @Resource
    private LogMapper logMapper;

    /**
     * 设置操作日志切入点   在注解的位置切入代码
     */
    @Pointcut("@annotation(com.baidu.shop.comment.MyLog)")
    public void logPointCut(){}

    /**
     *
     * @param joinPoint 方法的执行点
     * @param result 方法的返回值
     */
    @Transactional
    @AfterReturning(returning = "result",value = "logPointCut()")
    public void saveRecordLog(JoinPoint joinPoint,Object result){
        //获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从RequestAttributes获取到HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest)requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        //从切面织入点通过反射获取织入点的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //获取到切入点所在的方法
        Method method = signature.getMethod();

        LogEntity logEntity = new LogEntity();

        logEntity.setOperationTime(new Date());//操作时间
        //获取操作
        MyLog annotation = method.getAnnotation(MyLog.class);
        if(null != annotation){
            logEntity.setModel(annotation.operationModel());//操作模块
            logEntity.setOperation(annotation.operation());//具体操作
            logEntity.setType(annotation.operationType());//操作类型
        }
        logEntity.setIp(GetIp.getIpAddress(request));//用户登陆的ip

        logEntity.setResult(result.toString());//方法的返回值
        logEntity.setOperation_method(request.getRequestURI());//访问的方法
        String cookieValue = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());
        if (null != cookieValue){
            try {
                UserInfo userInfo = JwtUtils.getInfoFromToken(cookieValue, jwtConfig.getPublicKey());
                logEntity.setUserId(userInfo.getId().longValue());//用户id
                logEntity.setUserName(userInfo.getUsername());//用户name
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logMapper.insertSelective(logEntity);
    }
}
