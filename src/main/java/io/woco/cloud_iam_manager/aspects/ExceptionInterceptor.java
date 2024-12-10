package io.woco.cloud_iam_manager.aspects;

import io.kubernetes.client.openapi.ApiException;
import io.woco.cloud_iam_manager.exceptions.NotExistsException;
import io.woco.cloud_iam_manager.exceptions.UnknownCloudProviderException;
import io.woco.cloud_iam_manager.utils.LogUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Aspect
@Configuration
public class ExceptionInterceptor {

    /**
     * Intercepts exceptions thrown by methods in the facade package and logs detailed information.
     *
     * @param joinPoint the join point providing reflective access to the intercepted method
     * @param ex        the ApiException thrown by the intercepted method
     */
    @AfterThrowing(pointcut = "execution(* io.woco.cloud_iam_manager.facade.*.*.*(..))", throwing = "ex")
    public void k8sApiClientInterceptorFacade(JoinPoint joinPoint, ApiException ex) {
        String message = "Method: '" + joinPoint.getSignature().toShortString() +
                ", Args: '" + Arrays.asList(joinPoint.getArgs()) +
                "', Response: '" + ex.getResponseBody() +
                "', Message: '" + ex.getMessage() +
                "'";

        LogUtil.error(message, joinPoint.getSignature().getDeclaringType());
    }

    /**
     * Intercepts methods in the facade package that throw a NotExistsException.
     * Logs detailed information about the method and the exception.
     *
     * @param joinPoint the join point providing reflective access to the method being intercepted
     * @param ex        the NotExistsException thrown by the intercepted method
     */
    @AfterThrowing(pointcut = "execution(* io.woco.cloud_iam_manager.facade.*.*.*(..))", throwing = "ex")
    public void NotExistsInterceptorFacade(JoinPoint joinPoint, NotExistsException ex) {
        String message = "Method: '" + joinPoint.getSignature().toShortString() +
                ", Args: '" + Arrays.asList(joinPoint.getArgs()) +
                "', Message: '" + ex.getMessage() +
                "', Description: '" + ex.getDescription() +
                "'";

        LogUtil.error(message, joinPoint.getSignature().getDeclaringType());
    }

    /**
     * Intercepts exceptions of type UnknownCloudProviderException thrown by methods in the facade package.
     * Logs the method signature, arguments, exception message, and description at the ERROR level.
     *
     * @param joinPoint the join point providing reflective access to the method being intercepted
     * @param ex        the UnknownCloudProviderException thrown by the intercepted method
     */
    @AfterThrowing(pointcut = "execution(* io.woco.cloud_iam_manager.facade.*.*.*(..))", throwing = "ex")
    public void UnknownCloudProviderInterceptorFacade(JoinPoint joinPoint, UnknownCloudProviderException ex) {
        String message = "Method: '" + joinPoint.getSignature().toShortString() +
                ", Args: '" + Arrays.asList(joinPoint.getArgs()) +
                "', Message: '" + ex.getMessage() +
                "', Description: '" + ex.getDescription() +
                "'";

        LogUtil.error(message, joinPoint.getSignature().getDeclaringType());
    }
}
