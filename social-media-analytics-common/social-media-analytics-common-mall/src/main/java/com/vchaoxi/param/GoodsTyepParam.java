package com.vchaoxi.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Data
public class GoodsTyepParam {



    @NotNull(groups = {Edit.class,Del.class,UpAndDown.class})
    private Integer id;
    private Integer agentId;
    private Integer shopId;
    private Integer parentId;

    @NotNull(groups = {Add.class,Edit.class})
    private String name;
    @NotNull(groups = {UpAndDown.class})
    private Integer status;


    private Integer serial;

    @NotNull(groups = {Sort.class})
    @Size(groups = {Sort.class},min = 1)
    private List<Integer> ids;






    public interface Add {}
    public interface Edit {}
    public interface Del {}
    public interface UpAndDown {}
    public interface Sort {}
}
