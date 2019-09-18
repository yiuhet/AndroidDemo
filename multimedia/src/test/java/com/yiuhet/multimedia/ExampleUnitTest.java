package com.yiuhet.multimedia;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(new InterceptorＡ());
        interceptors.add(new InterceptorB());
        interceptors.add(new InterceptorC());

        RealInterceptorChain request = new RealInterceptorChain(interceptors, 0, "request");

        request.proceed("request");
        assertEquals(4, 2 + 2);
    }

    public class RealInterceptorChain implements Interceptor.Chain {

        private List<Interceptor> interceptors;

        private int index;

        private String request;

        public RealInterceptorChain(List<Interceptor> interceptors, int index, String request) {
            this.interceptors = interceptors;
            this.index = index;
            this.request = request;
        }

        @Override
        public String request() {
            return request;
        }

        @Override
        public String proceed(String request) {
            if (index >= interceptors.size()) return null;

            //获取下一个责任链
            RealInterceptorChain next = new RealInterceptorChain(interceptors, index + 1, request);
            // 执行当前的拦截器
            Interceptor interceptor = interceptors.get(index);
            return interceptor.interceptor(next);
        }
    }

    public interface Interceptor {
        String interceptor(Chain chain);

        interface Chain {
            String request();

            String proceed(String request);
        }
    }


    public class InterceptorＡ implements Interceptor {
        @Override
        public String interceptor(Chain chain) {
            System.out.println("执行 Ａ 拦截器之前代码");
            String proceed = chain.proceed(chain.request());
            System.out.println("执行 A 拦截器之后代码 得到最终数据：" + proceed);
            return proceed;
        }
    }

    public class InterceptorB implements Interceptor {
        @Override
        public String interceptor(Chain chain) {
            System.out.println("执行 B 拦截器之前代码");
            String proceed = chain.proceed(chain.request());
            System.out.println("执行 B 拦截器之后代码 得到最终数据：" + proceed);
            return proceed;
        }
    }

    public class InterceptorC implements Interceptor {
        @Override
        public String interceptor(Chain chain) {
            System.out.println("执行 C 最后一个拦截器 返回最终数据");
            return "success";
        }
    }


}