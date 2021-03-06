package com.atguigu.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UsernameExistException;
import com.atguigu.gulimall.member.vo.MemberRegistryVo;
import com.atguigu.gulimall.member.vo.MemberVo;
import com.atguigu.gulimall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void registry(MemberRegistryVo memberRegistryVo) {
        MemberEntity memberEntity = new MemberEntity();
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());  //????????????????????????

        //???????????????????????????????????????????????????controller????????????????????????????????????
        checkPhoneUnique(memberRegistryVo.getPhone());
        checkUsernameUnique(memberRegistryVo.getUserName());

        memberEntity.setMobile(memberRegistryVo.getPhone());
        memberEntity.setUsername(memberRegistryVo.getUserName());
        memberEntity.setNickname(memberEntity.getNickname());
        //???????????????????????????
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(memberRegistryVo.getPassword());
        memberEntity.setPassword(encode);

        //?????????????????????

        baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0){
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0){
            throw new UsernameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();

        //1.??????????????????
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct)
                .or().eq("mobile", loginacct));
        if (memberEntity == null){
            return null;
        }else{
            //1.?????????????????????password????????????password??????
            String pwDB = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //2.??????????????????
            boolean matches = passwordEncoder.matches(password, pwDB);
            if (matches){
                return memberEntity;
            }else{
                return null;
            }
        }
    }

    /**
     * ??????????????????login
     * @param socialUser
     * @return
     */
    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        //???????????????????????????
        String uid = socialUser.getUid();//??????????????????????????????id
        //1.?????????????????????????????????????????????????????????
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (null != memberEntity){
            //?????????????????????????????????
            memberEntity.setSocialUid(socialUser.getUid());
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            baseMapper.selectById(memberEntity);
            return memberEntity;
        }
        //2.????????????????????????????????????????????????????????????????????????
        MemberEntity member = new MemberEntity();
        //3.?????????????????????????????????????????????????????????????????????
        Map<String,String> query = new HashMap<>();
        query.put("access_token",socialUser.getAccess_token());
        query.put("uid",socialUser.getUid());
        try{
             HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);
            if (response.getStatusLine().getStatusCode() == 200){
                //???????????? ?????????????????????????????????
                /**
                 *      id": 5337862180,
                 *     "idstr": "5337862180",
                 *     "class": 1,
                 *     "screen_name": "LM91699",
                 *     "name": "LM91699",
                 *     "province": "100",
                 *     "city": "1000",
                 *     "location": "??????",
                 *     "description": "",
                 *     "url": "",
                 *     "profile_image_url": "https://tvax2.sinaimg.cn/crop.0.0.996.996.50/005Pf7Nily8gbifz17bm5j30ro0roacq.jpg?KID=imgbed,tva&Expires=1636296834&ssig=JvFQmpy%2B%2F9",
                 *     "cover_image_phone": "http://ww1.sinaimg.cn/crop.0.0.640.640.640/549d0121tw1egm1kjly3jj20hs0hsq4f.jpg",
                 *     "profile_url": "u/5337862180",
                 *     "domain": "",
                 *     "weihao": "",
                 *     "gender": "m",
                 */
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");
                member.setUsername(name);
                member.setNickname(name);
                member.setLevelId(1L); //??????????????????
                member.setGender("m".equals(gender) ? 1 : 0);//??????

            }
        }catch(Exception e){}
        member.setSocialUid(socialUser.getUid());
        member.setAccessToken(socialUser.getAccess_token());
        member.setExpiresIn(socialUser.getExpires_in());
        baseMapper.insert(member);
        return member;
    }
}