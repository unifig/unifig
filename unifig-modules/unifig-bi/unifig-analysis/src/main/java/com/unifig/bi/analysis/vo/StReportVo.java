package com.unifig.bi.analysis.vo;

import lombok.Data;

/**
 * <p>
 * 报表bo
 * </p>
 *
 *
 * @since 2019-03-21
 */
@Data
public class StReportVo {

    private static final long serialVersionUID = 1L;

    private long installCount = 0l;

    private long registerCount = 0l;

    private long innetCount = 0l;

    private long bindingCount = 0l;

    private long onlineCount = 0l;

    private String staticDate;

}
