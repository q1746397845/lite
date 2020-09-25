package com.baidu.upload;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.status.HTTPStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName UploadController
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/1
 * @Version V1.0
 **/
@RestController
@RequestMapping(value = "upload")
public class UploadController extends BaseApiService {

    //windows 系统上传路径
    @Value(value = "${mingrui.upload.path.windows}")
    private String windowsPath;

    //linux 系统上传路径
    @Value(value = "${mingrui.upload.path.linux}")
    private String linuxPath;

    //图片服务器的地址
    @Value(value = "${mingrui.upload.img.host}")
    private String imgHost;

    @PostMapping
    public Result<String> uploadImg(@RequestParam MultipartFile file){

        //判断上传的文件是否为空
        if(file.isEmpty()){
            return this.setResultError("上传得文件为空");
        }

        //获取文件名
        String filename = file.getOriginalFilename();

        String path = "";

        //获取是什么操作系统
        String os = System.getProperty("os.name").toLowerCase();

        if(os.indexOf("win") != -1){
            path = windowsPath;
        }else if (os.indexOf("lin") != -1){
            path = linuxPath;
        }

        //防止文件名重复
        filename = UUID.randomUUID() + filename;

        //创建文件 路径+分隔符+文件名(windows 和 linux 分隔符不一样 所以用File.separator代替)
        File dest = new File(path + File.separator + filename);

        //判断文件夹是否存在,不存在就创建
        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);//上传
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
        //将文件名返回页面用于页面回显
        return this.setResult(HTTPStatus.OK,"upload  success!!!",imgHost + "/" +filename);
    }
}
