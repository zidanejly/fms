package com.jfhealthcare.common.base;


import org.apache.http.HttpStatus;


import lombok.Data;


/**
 * 返回数据
 * 
 * @author xujinma
 */
@Data
public class PageResponse extends BaseResponse{
	 /**
     * 当前页码
     */
	private int pageNum;
	/**
	 * 总页数
	 */
    private int pageSize;
	/**
	 * 总数量
	 */
	private long totalNum;
	
	/*public static PageResponse getSuccessPage(PageInfo<?> info){
		return getSuccessPage(info,null);
	}
	
	public static PageResponse getSuccessPage(PageInfo<?> info,String message){
		PageResponse page = new PageResponse();
		if(info == null){
			page.setPageNum(1);
			page.setPageSize(10);
			page.setTotalNum(0);
		}else{
			page.setPageNum(info.getPageNum());
			page.setPageSize(info.getPageSize());
			page.setData(info.getList());
			page.setTotalNum(info.getTotal());
		}
		page.setCode(HttpStatus.SC_OK);
		page.setMsg(message);
		return page;
	}*/
	
}
