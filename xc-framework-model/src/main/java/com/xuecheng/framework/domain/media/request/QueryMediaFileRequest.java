package com.xuecheng.framework.domain.media.request;

import com.xuecheng.framework.model.Request.RequestData;
import lombok.Data;

@Data
public class QueryMediaFileRequest extends RequestData {

    private String fileOriginalName;
    private String processStatus;
    private String tag;
}
