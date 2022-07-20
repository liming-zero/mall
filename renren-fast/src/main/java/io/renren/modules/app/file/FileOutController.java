package io.renren.modules.app.file;

import cn.hutool.core.map.MapUtil;
import io.netty.util.internal.ObjectUtil;
import io.renren.modules.app.entity.UserEntity;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileOutController {

    @RequestMapping(value = "/download")
    public void download(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserEntity lmtAgrmInf = null;
        if(lmtAgrmInf != null){
            response.setHeader("content-type", "text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write("<script type='text/javascript'>alert('未找到该文件!');</script>");
            out.flush();
            out.close();
            return;
        }

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("tranCode", "2005");
        header.put("sysId", "SCM");
        header.put("sysKey", "imageKey");
        header.put("busiNo", "");
        header.put("fileId", "");
        Map<String, Object> result = new HashMap<>();
        if(!"0".equals(MapUtil.getStr(result,"tradeResult"))){
            response.setHeader("content-type", "text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write("<script type='text/javascript'>alert('"+ MapUtil.getStr(result,"tradeDesc") + "');</script>");
            out.flush();
            out.close();
            return;
        }

        String fileNm = "";

        //转换文件名称
        String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
        if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) {  //IE浏览器和Edge浏览器
            fileNm = URLEncoder.encode(fileNm.replaceAll(" ", ""), "UTF-8");
        } else {  //其他浏览器
            fileNm = new String(fileNm.getBytes("UTF-8"), "ISO8859-1");
        }
        String base64 = MapUtil.getStr(result,"base64");
        byte[] buffer = Base64.decodeBase64(base64);
        //response.setContentType("application/x-www-form-urlencoded");
        response.setHeader("Content-Disposition", "attachment; filename=" + "\"" + new String(fileNm.getBytes("UTF-8")) + ".pdf\"");
        OutputStream os = response.getOutputStream();
        os.write(buffer);
        os.flush();
        os.close();
    }
}
