package com.chargehub.thirdparty.api.domain.vo.invoice;


import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 15:14
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.invoice
 * @Filename：InvoicingVo
 */
@Data
public class InvoicingVo {


    /**
     * 发票申请结果
     */
    private Boolean result;

    /**
     * 错误提示
     */
    private String errMsg;


    public InvoicingVo() {
    }

    /**
     * 有参构造
     */
    public InvoicingVo(Boolean result, String errMsg) {
        this.result = result;
        this.errMsg = errMsg;
    }


    /**
     * 成功
     * @return
     */
    public static InvoicingVo success(){
        return new InvoicingVo(true,"OK");
    }


    /**
     * 失败
     * @return
     */
    public static InvoicingVo fail(String errMsg){
        return new InvoicingVo(false,errMsg);
    }
}
