import com.baidu.shop.repository.RedisRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @ClassName SpringBootApplicationTests
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/27
 * @Version V1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootApplicationTests.class})
public class SpringBootApplicationTests {

    @Resource
    private RedisRepository redisRepository;

    @Test
    public void test(){

        Integer id = 1;
        Integer category = 1;
        Integer heat = 0;
        redisRepository.setHash(id+ "",category + "",heat + "");

    }
}
