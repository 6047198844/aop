package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

@Slf4j
public class ExecutionTest {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;
    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    @Test
    void printMethod() {
        //=public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        log.info("helloMethod={}", helloMethod);
    }

    @Test
    void exactMatch() {
        //=public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void allMatch() {
        //=public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(* *(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatch() {
        //=public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(* hello(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar1() {
        //=public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(* hel*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar2() {
        //=public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(* *el*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStarFalse() {
        //=public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(* nono(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactMatch1() {
        //=public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(* nono(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void typeExactMatch() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void typeMatchSuperType() {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 자식 타입까지는 매칭이 된다.
    @Test
    void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");

        final Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        Assertions.assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void argsMatch() {
        pointcut.setExpression("execution(* *(String))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 파라미터가 없어야 함
    @Test
    void argsMatchNoArgs() {
        pointcut.setExpression("execution(* *())");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    // 정확히 하나의 파라미터 허용, 모든 타입 허용
    @Test
    void argsMatchStar() {
        pointcut.setExpression("execution(* *(*))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 숫자와 무관하게 모든 파라미터 허용, 모든 타입 허용
    @Test
    void argsMatchAll() {
        pointcut.setExpression("execution(* *(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // String 타입으로 시작 .. 은 0-N
    @Test
    void argsMatchComplex() {
        pointcut.setExpression("execution(* *(String, ..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void withinExact() {
        pointcut.setExpression("within(hello.aop.member.MemberServiceImpl)");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void withinStart() {
        pointcut.setExpression("within(hello.aop.member.*Service*)");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void withinSubPackage() {
        pointcut.setExpression("within(hello.aop..*)");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("타겟의 타입에만 직접 적용, 인터페이스를 선정하면 안된다.")
    void withinSuperTypeFalse() {
        pointcut.setExpression("within(hello.aop.member.MemberService)");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("execution은 타입 기반, 인터페이스를 선정 가능")
    void executionSuperTypeTrue() {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
}
