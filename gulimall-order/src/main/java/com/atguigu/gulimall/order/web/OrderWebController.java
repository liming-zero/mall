package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.exception.NoStockException;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.SubmitOrderRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
@Slf4j
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }

    /**
     * 下单功能
     *
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("订单提交的数据" + vo);
        try {
            SubmitOrderRespVo respVo = orderService.submitOrder(vo);
            if (respVo.getCode() == 0) {
                //下单成功来到支付选择页
                model.addAttribute("submitOrderResp", respVo);
                return "pay";
            } else {
                String msg = "下单失败：";
                switch (respVo.getCode()) {
                    case 1:
                        msg += "订单信息过期，请刷新页面再提交";
                        break;
                    case 2:
                        msg += "订单商品价格发生变化，请确认后再次提交";
                        break;
                }
                redirectAttributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.gulimall.com/toTrade";
            }
        } catch (Exception e) {
            if (e instanceof NoStockException) {
                String message = ((NoStockException) e).getMessage();
                log.warn(message);
                redirectAttributes.addFlashAttribute("msg", message);
            }else{
                log.error("系统异常:{}",e.getMessage());
                redirectAttributes.addFlashAttribute("msg", "服务不可用，请稍后再试！");
                e.printStackTrace();
            }
            return "redirect:http://order.gulimall.com/toTrade";
        }

        //下单，去创建订单，验令牌，验价格，锁库存.....
        //下单成功来到支付选择页
        //下单失败回到订单确认页重新确认订单信息
    }
}
