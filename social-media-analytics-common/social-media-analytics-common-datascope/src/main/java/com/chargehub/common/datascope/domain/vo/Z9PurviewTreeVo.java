package com.chargehub.common.datascope.domain.vo;

import com.chargehub.common.datascope.domain.PurviewTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhanghaowei
 * @since 2023-12-14 23:19
 */
@Data
@Schema
@AllArgsConstructor
public class Z9PurviewTreeVo implements Serializable {
    private static final long serialVersionUID = 1178725505214707128L;

    @Schema(description = "权限树列表")
    private List<PurviewTree> purviewTreeList;

    @Schema(description = "被选中的id集合")
    private List<PurviewTree> checkedList;

    @Schema(description = "是否全部数据权限")
    private boolean all;

    public Z9PurviewTreeVo() {
        this.purviewTreeList = new ArrayList<>();
        this.checkedList = new ArrayList<>();
    }
}
