package com.spring.reggie.controller;
import com.spring.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    /**
     * 文件上传
     * **/
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件,需要转存到指定位置，否则本次请求完成后临时文件会被删除
        //upload方法名中的参数名 必须是file（文件上传表单的 中name属性值必须是file,name="file"）
        log.info("上传的文件为: "+file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();  //abc.jpg
        //截取原始文件名的后缀（使用UUID+原始文件名的后缀以防上传文件名重复）
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));  // suffix = .jpg  截取是带点的后缀

        // 使用UUID重新生成文件名，防止文件名重复，造成后面上传的文件覆盖前面上传的文件
        String fileName = UUID.randomUUID().toString()+suffix; //随机生成的30多位+后缀

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //如果目录不存在则创建
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);

    }

    //文件下载
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));

            //输出流,通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置一下为图片文件
            response.setContentType("image/jpeg");

            // 输入流读取到 内容放到 bytes数组中
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){   //当为-1的时候输入流读取完成
                outputStream.write(bytes,0,len);          //写输入流到浏览器
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



