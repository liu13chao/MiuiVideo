#ifndef __INTTERFACE_H__
#define __INTTERFACE_H__

//API 接口版本号
#define APIVERSION      "1.8"

//app error code
#define				CURL_OTHER_ERROR		-1000		//curl 报告的其他错误 , 如果获得的返回值小于-1000 加上1000获得的值为libcurl返回的值
#define				UNKONW_ERROR			-99			//未知错误
#define				LIMIT_IS_EXCEEDED       -30         //超过限制
#define				INVALID_LOCAL_PARAM	    -29			//错误的参数(本地接口返回)
#define				USER_IN_BLACKLIST		-28			//该用户在黑名单中
//#define				CURL_ERROR				-27			//curl 报告的其他错误，或者返回的数据为null
#define				FILE_NOT_EXIST			-26			//文件不存在
#define				GENERATE_SECURITY_FAILED -25		//生成SECURITY失败
#define				ILLEGAL_ACCOUNT			-24			//不合法的account
#define				ILLEGAL_PASS_TOKEN		-23			//不合法的passtoken
#define				API_VER_ILLEGAL			-22			//不合法的版本号
#define				API_VER_NOT_MATCH		-21			//版本号不匹配
#define				INVALID_CELEBRITY_ID	-20			//错误的影人id
#define				USER_AUTH_FAILED		-19			//token 或者key错误
#define				DATA_NOT_INIT			-18			//没有初始化接口
#define				INVALID_MEDIA_ID		-17			//错误的mediaid
#define				USER_NOT_LOGIN			-16			//没有调用登陆接口
#define				JSON_PARSE_ERROR		-15			//json parse error
#define				INVALID_WEB_PARAM		-13			//错误的参数(webserver 返回)
#define				CHANNEL_ERROR		    -12			//频道号错误
#define				NO_AVAILABLE_SOURCE     -11			//没有可用的源
#define				MEMORY_LOW              -10			//内存不足
#define				TASK_QUEUE_FULL			-9			//任务队列满
#define				SESSION_TIMEOUT			-8			//会话超时
#define				TIME_NOT_SYNC			-7			//时间不同步
#define				EXCEED_MAX_ROWS			-6			//超过最大条数限制
#define				KEY_CHECK_FAILED		-5			//key check error
#define				LIBINIT_ERROR			-4			//初始化库错误
#define				DEVICECODE_ERROR		-3			//设备码错误
#define				TIMEOUT_ERROR			-2			//超时
#define				CONNECT_ERROR			-1			//连接错误
#define				EMPTY_RESULT			0			//没有得到结果
#define				NO_ERROR				1			//没有错误


/*login 接口 平台定义*/
#define				PLATFORM_APPLE_TV_iOS_4		0
#define				PLATFORM_APPLE_TV_iOS_5		1

#define				PLATFORM_LINUX_MIPS			100
#define				PLATFORM_LINUX_ARM			101



#define				PLATFORM_ANDROID_MI_RED_TWO             195
#define				PLATFORM_ANDROID_PHONE_SUPER_RESOLUTION 196
#define				PLATFORM_ANDROID_MI_THREE				197
#define				PLATFORM_ANDROID_PHONE_LOW_RESOLUTION	198
#define				PLATFORM_ANDROID_PHONE_HIGH_RESOLUTION	199
#define				PLATFORM_ANDROID_MI_ONE					200
#define				PLATFORM_ANDROID_MI_TWO					201
#define				PLATFORM_ANDROID_AMLOGIC				202
#define				PLATFORM_ANDROID_AMLOGIC_BESTV			203
#define				PLATFORM_ANDROID_AMLOGIC_CNTV			204
#define				PLATFORM_ANDROID_AMLOGIC_CNTV_M6		205
#define				PLATFORM_ANDROID_QUALCOMM_BESTV			206
#define				PLATFORM_ANDROID_AMLOGIC_TV				601


/*雷石login 接口 平台定义*/
#define				PLATFORM_THUNDER_IOS		1000
#define				PLATFORM_THUNDER_ANDROID	1001
#define				PLATFORM_THUNDER_ARMLINUX	1002

/*广电总局平台号定义*/
#define				PLATFORM_ANDROID_AMLOGIC_SARFT_WASU		10101
#define				PLATFORM_ANDROID_AMLOGIC_SARFT_BESTV	10102

/*
查找方式定义
*/

//nMediaNameSearchType 参数

#define		SEARCH_MEDIA_BY_PY            1			// 按名字搜索除 MV 外的视频
#define		SEARCH_MV_BY_PY               2			// 按名字搜索 MV
#define		SEARCH_CINEASTE_BY_PY		  3			// 按人名搜索除 MV 外的影人
#define		SEARCH_SINGER_BY_PY			  4			// 按歌手名搜索 MV影人

#define		SEARCH_BY_PY				10
#define		SEARCH_BY_FUZZY_PY			20
#define		SEARCH_BY_SYMBOL			30


#define		SEARCH_MEDIA_BY_FUZZY_PY        101		//按T9键盘名字搜索除 MV 外的视频
#define		SEARCH_MV_BY_FUZZY_PY           102		//按T9键盘名字搜索 MV
#define		SEARCH_CINEASTE_BY_FUZZY_PY     103		//按T9键盘人名搜索除 MV 外的影人
#define		SEARCH_SINGER_BY_FUZZY_PY       104		// 按T9键盘歌手名搜索 MV影人

#define		SEARCH_MEDIA_BY_SYMBOL			201
#define		SEARCH_MV_BY_SYMBOL				202
#define		SEARCH_CINEASTE_BY_SYMBOL		203
#define		SEARCH_SINGER_BY_SYMBOL			204

#define		SEARCH_MEDIA_BY_KEYWORD			1001	// 按关键字搜索除 MV 外的视频
#define		SEARCH_MV_BY_KEYWORD			1002	// 按关键字搜索 MV
#define		SEARCH_CINEASTE_BY_KEYWORD		1003	// 按关键字搜索除 MV 外的影人
#define		SEARCH_SINGER_BY_KEYWORD		1004	// 按关键字搜索 MV 影人

//nSearchMask 参数
#define		SEARCH_MASK_ALL			  0			// 全部
#define		SEARCH_MASK_NAME          1			// 按名字搜索
#define		SEARCH_MASK_DIRECTOR      2			// 按导演搜索
#define		SEARCH_MASK_ACTOR         4			// 按演员/歌手/主持搜索
#define		SEARCH_MASK_MOVIE         1024		// 在电影中搜索
#define		SEARCH_MASK_TV            2048		// 在电视剧中搜索
#define		SEARCH_MASK_CARTOON       4096		// 在动漫中搜索
#define		SEARCH_MASK_SYNTHESIS     8192		// 在综艺中搜索
#define		SEARCH_MASK_DOCUMENTARY   16384		// 在纪录片中搜索
#define		SEARCH_MASK_MUSIC_VIDEO   32768		// 在音乐中搜索
#define		SEARCH_MASK_EDUCATION	  65536		// 在教育中搜索


/*
uVideoCapabilities 的填充值
*/
#define			VIDEO_CAPABILITY_MP4			0x00000001
#define			VIDEO_CAPABILITY_FLV			0x00000002
#define			VIDEO_CAPABILITY_TS				0x00000004

/*
uAudioCapabilities 的填充值
*/
#define			AUDIO_CAPABILITY_MP3			0x00000001
#define			AUDIO_CAPABILITY_WMA			0x00000002
#define			AUDIO_CAPABILITY_WAV			0x00000004

/*
uImageCapabilities 的填充值
*/
#define			IMAGE_CAPABILITY_BMP			0x00000001
#define			IMAGE_CAPABILITY_JPG			0x00000002
#define			IMAGE_CAPABILITY_GIF			0x00000004


//清晰度定义
#define			VIDEO_SHARPNESS_NORMAL			1
#define			VIDEO_SHARPNESS_HIGH			2
#define			VIDEO_SHARPNESS_SUPER			3


/*
GETMEDIAINFOLIST 结构中		orderby 变量的参数
*/
#define			ORDER_BY_UPDATETIME_DESC	0
#define			ORDER_BY_HOT_DESC			1
#define			ORDER_BY_ISSUEDATE_DESC		2
#define			ORDER_BY_UPDATETIME_ASC		3
#define			ORDER_BY_HOT_ASC			4
#define			ORDER_BY_ISSUEDATE_ASC		5
#define			ORDER_BY_SCORE_DESC			6
#define			ORDER_BY_SCORE_ASC			7


/*
GETMEDIAINFOLIST 结构中		listtype 变量的参数
*/
#define			MEDIA_LIST_TYPE_LIST	 0
#define			MEDIA_LIST_TYPE_ICON	 1

/*
GETMEDIAINFOLIST 结构中		postertype 变量的参数
*/
#define			POSTER_TYPE_BIG			 0
#define			POSTER_TYPE_SMALL		 1

/*nMediaType 类型定义*/
#define			ID_TYPE_MEDIA				0
#define			ID_TYPE_PERSON				1
#define			ID_TYPE_ACTOR_DIRECTOR		2
#define			ID_TYPE_SPECIALSUBJECT		100
#define			ID_TYPE_TELEVISION			200

/*
LPMEDIAINFO 结构中 nFlag 类型，或运算
*/
#define			MEDIA_NO_FLAG			0x00000000
#define			MEDIA_NEW_FLAG			0x00000001
#define			MEDIA_HOT_FLAG			0x00000002
#define			MEDIA_UPDATE_FLAG		0x00000004

/*
LPMEDIAINFO 结构中 nResolution 类型，或运算
*/
#define			MEDIA_NORMAL			0x00000001
#define			MEDIA_HIGH				0x00000002
#define			MEDIA_SUPER				0x00000004


/*filter type定义*/
#define			EXCLUDE_MV_FILTER       1
#define			ONLY_INCLUDE_MV_FILTER  2

/*gettvchannelrecommendmedia  nBackGroundColor 定义*/
#define COLOR_CORLOURLESS			0 //无色 
#define COLOR_ORANGE				1 //橙色 
#define COLOR_RED					2 //红色 
#define COLOR_GREEN					3 //绿色 
#define COLOR_BLUE					4 //蓝色 
#define COLOR_YELLOW			    5 //黄色

/*
url信息
*/
#define		URL_MAX_COUNT				20 //每种清晰度url最大个数

/*
DK_GetUpgradeInfo 接口中 nUpgradeType定义
*/
#define UPGRADE_TYPE_AUTO		0		//自动升级
#define UPGRADE_TYPE_MANUAL		1		//手动升级

/*
DK_SetUpgradeInfo 接口中 nSeccuss定义
*/
#define	UPGRADE_SUCCESS			1		//更新成功
#define	UPGRADE_FAILED			0		//更新失败

/*
LPVARIETYLIST 中syle的变量
*/
#define		YEAR_STYLE					1
#define		TRADITIONAL_STYLE			0

/*media type*/
#define		ID_TYPE_MEDIA				0
#define		ID_TYPE_PERSON				1


/*nAlignment 定义*/
#define		ALIGNMENT_TOP				1
#define		ALIGNMENT_B0TTOM			2
#define		ALIGNMENT_LEFT				3
#define		ALIGNMENT_RIGHT				4
#define		ALIGNMENT_CENTER			5

/*http auth*/
#define PROXY_AUTH_NONE         0       /* nothing */
#define PROXY_AUTH_BASIC        (1<<0)  /* Basic (default) */
#define PROXY_AUTH_DIGEST       (1<<1)  /* Digest */
#define PROXY_AUTH_GSSNEGOTIATE (1<<2)  /* GSS-Negotiate */
#define PROXY_AUTH_NTLM         (1<<3)  /* NTLM */
#define PROXY_AUTH_DIGEST_IE    (1<<4)  /* Digest with IE flavour */
#define PROXY_AUTH_ONLY         (1<<31) /* used together with a single other
                                         type to force no auth or just that
                                         single type */
#define PROXY_AUTH_ANY			(~PROXY_AUTH_DIGEST_IE)  /* all fine types set */
#define PROXY_AUTH_ANYSAFE		(~(PROXY_AUTH_BASIC|PROXY_AUTH_DIGEST_IE))


typedef struct tagURLINFO
{
	char szMd5[34];	//图片的md5
	char szURL[256];	//图片的地址
}URLINFO, *LPURLINFO;

struct tagCHANNELINFOLIST;
struct tagCHANNELINFO;

typedef struct tagPOSTER
{
	int nPosterX;		//图片左下标X
	int nPosterY;		//图片左下标Y
	URLINFO poster;	//图片的地址
}POSTER, *LPPOSTER;

typedef struct tagBANNER
{
	int     nAlignment;
	URLINFO poster;	//图片的地址
}BANNER, *LPBANNER;

typedef struct tagRECOMMENDPOSTERINFOR
{
	int nPosterType;    //图片类型
	int nPosterPosition;//图片开始位置
	int	nPosterCount;	//图片信息结构的数量
	LPPOSTER lpPosterList;//图片信息指针
	LPBANNER lpBanner;	  //banner信息
	LPURLINFO lpIcon;	  //icon信息
}POSTERINFOR, *LPPOSTERINFOR;

/*
频道信息列表
*/
typedef struct tagCHANNELINFOLIST
{
	int nDataCount;
	int nVersion;
	struct tagCHANNELINFO* lpChannelInfo;

}CHANNELINFOLIST, *LPCHANNELINFOLIST;

typedef struct tagPRODUCTPOSTERINFO
{
    char szMd5[34]; //图片的md5
    char szURL[256]; //图片的地址
    int  type;
}PRODUCTPOSTERINFO, *LPPRODUCTPOSTERINFO;

/*
 付费频道的产品代码，每个产品代码对应着相应的付费策略
 */
typedef struct tagPRODUCTCODE
{
    char name[128];
    int  type;          //1--单点视频; 2--视频包(包月/包季...)
    int  nPosterCount;
    LPPRODUCTPOSTERINFO lpPosters;
} PRODUCTCODE, *LPPRODUCTCODE;


/*
付费频道的产品代码列表
 */
typedef struct tagPRODUCTCODELIST
{
    int nCount;
    LPPRODUCTCODE lpProductCode;
} PRODUCTCODELIST, *LPPRODUCTCODELIST;

/*
频道信息结构体
*/

struct tagCHANNELINFO
{
    int nChannelId;                                 //频道id
    char channelname[32];                           //频道的名字
    int  channeltype;                               //子分类信息数量，没有的话为0,有的话为1
    int  category;                                  //0--普通channel;1--专题专区(只能有1个);2--收费channel(有product字段)
    LPPOSTERINFOR lpPosterInfo;                     //图片信息
    LPPRODUCTCODELIST lpProductCodeList;            //付费频道的产品代码列表
    int  nCount;                                    //如果channeltype 为1的话该字段表示该频道下有多少部电影，如果channeltype为0的话 改字段为0
    CHANNELINFOLIST SubChannelInfoList;             //子频道
};

typedef struct tagCHANNELINFO *LPCHANNELINFO;
typedef struct tagCHANNELINFO CHANNELINFO;

/*
电影的详细信息
*/
typedef struct tagMEDIADETAILINFO
{
	int				playcount;					//播放次数
    int				setcount;					//影片总集数
	int				currentcount;				//当前有效集数
	int				playlength;					//此电影播放时间
	char			issuedate[16];				//上映时间
	char			desc[1024];					//影片描述
	char			szArea[32];					//地区
	char			szLanguage[32];				//语言
	URLINFO		    bigPoster;			        //大海报图片地址
	URLINFO		    smallPoster;		        //小海报图片地址
	float			score;						//评分
	int				nScoreCount;				//

}MEDIADETAILINFO, *LPMEDIADETAILINFO;

typedef struct tagNAMEINDEX
{
	int  nValidLength;
	char *lpszFuzzyCapital;
	char *lpszSpelling;
	char *lpszCapital;
	char *lpszSymbol;
}NAMEINDEX, *LPNAMEINDEX;

/*
电影基础信息
*/
typedef struct tagMEDIAINFO
{
	int				nMediaID;						//影片id
	int				nFlag;
	int				nResolution;					//标记该电影的清晰度
	int				playlength;						//此电影播放时间
	int				nSetNow;						//当前集数
	int				nSetCount;						//影片总集数
	int				nPlayCount;						//播放次数
	float			score;							//评分
	int				nScoreCount;					//
	char			szIssuedate[16];				//上映时间
	char			szLatestissuedate[16];			//最后更新时间
	char			szCategory[32];					//媒体类型
	char			szTags[32];						//电影所属类别
	char			szMediaName[128];				//标题
	char			szDirector[128];				//导演
	char			szActors[512];					//演员
    URLINFO			poster;							//封面图片小
	char			szArea[32];						//区域
	char			szLanguage[32];					//语言
	int				nIsMultipleSet;					//
	LPNAMEINDEX		lpNameIndex;					//搜索关键字索引

}MEDIAINFO, *LPMEDIAINFO;



typedef struct tagCATEGORYINFO
{
	char	szCategory[32];
	int		nMediaCount;
	int		nSearchMask;
}CATEGORYINFO, *LPCATEGORYINFO;
/*
电影信息列表
*/
typedef struct tagMEDIAINFOLIST
{
	int nTotalCount;
	int nCategoryArrayDataCount;	//表示lpCateGoryInfo数据个数
	int nMediaInfoArrayDataCount;	//表示lpMediaInfo数据个数
	int nRecommendArrayDataCount;   //表示lpRecommend数据个数
	LPMEDIAINFO		lpMediaInfo;
	LPCATEGORYINFO	lpCateGoryInfo;
	LPMEDIAINFO     lpRecommend;
}MEDIAINFOLIST, *LPMEDIAINFOLIST;

/*
手机电视直播
*/
typedef struct tagTELEVISION
{
	int				nTVID;
	char			szPlayId[32];
	char			szTVName[64];
	int				nBackGroundColor;
	URLINFO			poster;	
	int             nRank;
	int             nMediaType;
}TELEVISION, *LPTELEVISION;

typedef struct tagTVDATA
{
    int nDataCount;
    LPTELEVISION lpTvDataInfos;
} TVDATA, *LPTVDATA;

/*
频道推荐
*/
typedef struct tagCHANNELRECOMMENDMEDIA
{
	int nIsManual;					//如果是人工推荐则为1，机器推荐为0
	int nChannelId;					//频道id
	int nTotalCount;				//该频道总共的电影数量
	int nMediaType;					//如果nMediaType为ID_TYPE_MEDIA表示lpMediaInfo, ID_TYPE_TELEVISION表示lpTVInfo有数据
	int nMediaInfoArrayDataCount;	//表示lpMediaInfo或lpTVInfo有多少个
	LPMEDIAINFO   lpMediaInfo;
	LPTELEVISION  lpTVInfo;

}CHANNELRECOMMENDMEDIA, *LPCHANNELRECOMMENDMEDIA;

/*
频道推荐列表
*/
typedef struct tagCHANNELRECOMMENDMEDIALIST
{
	int nTotalCount;				//返回的频道数量
	LPCHANNELRECOMMENDMEDIA	 lpMediaInfo; //每个频道对应的推荐

}CHANNELRECOMMENDMEDIALIST, *LPCHANNELRECOMMENDMEDIALIST;

/*
排行榜列表具体信息
*/
typedef struct tagRANKINGLISTINFO
{
	int nChannelId;					//频道id
	char szChannelName[32];			//频道的名字
	int nTotalCount;				//该频道总共的电影数量
	int nMediaInfoArrayDataCount;	//表示lpMediaInfo有多少个
	LPMEDIAINFO   lpMediaInfo;

}RANKINGLISTINFO, *LPRANKINGLISTINFO;

typedef struct tagRANKINGLIST
{
	int nVersion;
	int nTotalCount;						//返回的频道数量
	LPRANKINGLISTINFO	 lpRankinglistInfo; //每个频道对应的推荐

}RANKINGLIST, *LPRANKINGLIST;

/*
电影查找结构
*/
typedef struct tagSEARCHINFO
{
	char			szMediaName[256];			//电影名字
	int				nMediaNameSearchType;		//电影名字是否按照拼音方式查找
	int				nSearchMask;				//不使用请填写-1
	int				nPageNo;					//第几页
	int				nPageSize;					//每页数量
	int				nPosterPlatform;			//nPosterPlatform不用设置为小于等于0的数
	char			szUserBehavData[512];

}SEARCHINFO, *LPSEARCHINFO;

typedef struct tagTOKENINFO
{
	char szKey[34];
	char szToken[34];
}TOKENINFO, *LPTOKENINFO;

typedef struct tagINITCONNECTION
{
	char			szClientVer[16];				//客户端版本
	char			szAppVersion[16];				//客户端显示用的版本号
	int				nCodeVersion;					//Code的版本号
	char			szDeviceID[34];					//客户端设备号
	char			szUserID[128];					//用户id
	char			szModelInfo[64];				//客户端型号， 手机上传手机的具体型号信息
#ifdef USING_IPAD
	char			szM3U8Path[512];				//m3u8临时文件的地方
	char			szPythonPath[512];				//放PYTHON文件的地方
	char			szCookieFile[512];				//放CookieFile文件的地方
#else
	char			szM3U8Path[64];					//m3u8临时文件的地方
	char			szPythonPath[64];				//放PYTHON文件的地方
	char			szCookieFile[64];				//放CookieFile文件的地方
#endif
	char			szUserAgent[128];				//useragent http
	char			szTVServiceURL[64];				//TVservice服务器url
	char			szUpgradeURL[64];				//upgrade服务器url
	char			szUploadLogURL[64];				//日志服务器url
    char            szWeatherCityURL[64];           //url for weather city info
	int				nPlatfrom;						//平台定义
	int				nRedirectPlatform;				//平台定义
	unsigned int	uVideoCapabilities;				//客户端视频能力
	unsigned int	uAudioCapabilities;				//客户端音频能力
	unsigned int	uImageCapabilities;				//客户端图片能力
	int				nMaxMediaDescribeLength;		//电影描述的最大长度
	int				nIncremental;					//升级时需要
    char			szSystemRelease[16];
    char			szBuildType[16];
	char			szBuildID[32];                  //build id
    char			szProduct[32];
	char			szProxyAddress[64];				//如果需要代理登陆设置
	char			szProxyUserNamePassWord[64];	//如果需要代理登陆设置 没有0填充
	int				nHttpAuth;
	TOKENINFO		tokenInfo;
}INITCONNECTION, *LPINITCONNECTION;


typedef struct tagGETMEDIAINFOLIST
{
	int		arnIDs[32];					//想要获得的的id
	int		nIDsCount;					//id 数量
	int		nPageNo;					//第几页
	int		pagesize;					//每页大小
	int		orderby;					//排序
	int		listtype;					//
	int		postertype;					//
	int		nMediaNameSearchType;		//不使用请填写-1  目前只有DK_GetFilterMediaInfo接口支持查找
	char	szSearchMediaName[256];		//电影名字， 不使用请0填充
	char	szUserBehavData[512];		//用户行为统计数据

}GETMEDIAINFOLIST, *LPGETMEDIAINFOLIST;

typedef struct tagGETMEDIABYCELEBRITY
{
	int nCelebrityId;
	int nFilterType;
	int nPageNo;
	int nPageSize;
	int nOrder;
	int nPosterType;
	int nListType;
}GETMEDIABYCELEBRITY, *LPGETMEDIABYCELEBRITY;

typedef struct tagGETMEDIAVARIETY
{
	int nMediaId;
	int nYear;
	int nPageNo;
	int nPageSize;
	int nOrder;
	int	nPosterPlatform;			//不使用时要填写<=0的值
}GETMEDIAVARIETY, *LPGETMEDIAVARIETY;


typedef struct tagMEDIA3DSETTING
{
    int mode; //0 -> 非3D, 1 -> 左右, 2 -> 上下, 3 -> 侦封装
} MEDIA3DSETTING;

typedef struct tagMEDIASETTING
{
    MEDIA3DSETTING media3Dsetting;
} MEDIASETTING;

typedef struct tagMEDIAURLINFO
{
	int  nMediaSource;				//视频源
	int  nStartOffset;				//开始偏移
	int  nEndOffset;				//结束偏移
	int	 nIsHtml;
	int  nIsRealUrl;                //标志当然的url是否是真实的地址， 1 为真实地址，0为需要客户端再次获取
	char szMediaUrl[1024];			//视频的播放地址
    MEDIASETTING setting;           //场景、3d等设置
    int  nMediaId;                  //cntv media id
    char szContentId[64];           //content id
}MEDIAURLINFO, *LPMEDIAURLINFO;

typedef struct tagMEDIAURLINFOLIST
{
	char		 szVideoName[256];
	MEDIAURLINFO urlNormal[URL_MAX_COUNT];				//标清播放地址
	MEDIAURLINFO urlHigh[URL_MAX_COUNT];				//高清播放地址
	MEDIAURLINFO urlSuper[URL_MAX_COUNT];				//超清播放地址
}MEDIAURLINFOLIST, *LPMEDIAURLINFOLIST;


typedef struct tagNETWORKSPEEDURL
{
	int  nMediaSource;				//视频源
	int  nStartOffset;				//开始偏移
	int  nEndOffset;				//结束偏移
	int	 nIsHtml;
	int  nIsRealUrl;                //标志当然的url是否是真实的地址， 1 为真实地址，0为需要客户端再次获取
	char szSourceName[32];			//视频源名字
	char szHttpHeader[1024];		//http header
	char szMediaUrl[1024];			//视频的播放地址

}NETWORKSPEEDURL, *LPNETWORKSPEEDURL;

typedef struct tagNETWORKSPEEDURLLIST
{
	int nDataCount;
	LPNETWORKSPEEDURL lpUrlInfoList;
}NETWORKSPEEDURLLIST, *LPNETWORKSPEEDURLLIST;
/*
个人信息简介
*/
typedef struct tagPERSONINFOR
{
	int			nPersonId;				//personId
	char		szCountry[32];			//个人所属地区
	char		szcName[64];			//中文名称
	char		szeName[64];			//英文名称
	char		szAlias[64];			//别名
	URLINFO	poster;					//海报
	char		szCv[2048];				//简介
}PERSONINFOR, *LPPERSONINFOR;

/*
播放历史记录
*/
typedef struct tagPLAYHISTORY
{
	MEDIAINFO       mediaInfo;				//视频id				//上传必填   //删除填写
	LPPERSONINFOR	lpPersonInfor;			//个人信息简介，当 nMediaType不为 0的时候改指针有效
	int				nMediaCi;				//视频第几集			//上传必填   //删除填写  //当上传影人信息时，ci填写mediaid， mediaid填写影人的id， nMediaType为非0
	int				nMediaSource;			//视频源				//上传必填
	char			szVideoName[128];		//视频名字
	float			fPercent;				//百分比				//上传必填
	float			fMoviePercent;			//当前剧集百分比
	char			szPlaydate[32];			//播放日期			//上传必填
	int				nScoreCount;			//
	float			score;					//评分
	int				nSetNow;				//当前有效集数
	int				nDuration;				//					//上传可填 默认-1
	int				nMediaType;				//当前 media的类型 
	int				nTotalSet;				//总集数
	unsigned long	lPlayseconds;			//播放时间，从开始的播放的秒算起。			//上传必填
	int				nQuality;				//上传时填写，默认-1 影片清晰度
	int				nErrorCode;				//上传时填写，默认-1 影片播放失败原因 
	unsigned long	lCreateTime;			//上传时填写，从1970年到当前的秒
	char			*lpszPrivateData;		//app自定义数据。
	LPPRODUCTCODELIST lpProductCodeList;    //产品代码列表
}PLAYHISTORY, *LPPLAYHISTORY;

typedef struct tagPLAYHISTORYLIST
{
	int nDataCount;
	LPPLAYHISTORY lpPlayHistory;
}PLAYHISTORYLIST, *LPPLAYHISTORYLIST;

/*
收藏夹
*/
typedef struct tagBOOKMARK
{
	MEDIAINFO		mediainfo;	//当nMediaType为1的时候 mediainfo中的ci为mediaid， mediainfo中的mediaid为歌手id
	char			szDate[32];
	long		  	ltime;
	unsigned long	lCreateTime;			//上传时填写，从1970年到当前的秒
	char			*lpszPrivateData;		//app自定义数据。
}BOOKMARK, *LPBOOKMARK;


typedef struct tagBOOKMARKLIST
{
	int nDataCount;
	LPBOOKMARK lpBookMarks;
}BOOKMARKLIST, *LPBOOKMARKLIST;

typedef struct tagINPUTCHASENEW
{
	int				nMediaId;				//media id
	int				nCi;					//当前集数
	float			fPercent;				//看到的百分比 1表示100%
	unsigned long	lCreateTime;			//上传时填写，从1970年到当前的秒
}INPUTCHASENEW, *LPINPUTCHASENEW;
/*
追新结构体
*/
typedef struct tagCHASENEW
{
	MEDIAINFO		mediainfo;	//当nMediaType为1的时候 mediainfo中的ci为mediaid， mediainfo中的mediaid为歌手id
	int				nSetCount;	//总集数
	int				nSetNow;	//当前更新集数
	int				nUserCi;	//用户看到的集数
	int				nRemain;	//用户剩下没看的集数
	float			fPercent;	//还剩下多少没看
	long			ltime;
	char			szDate[32];
}CHASENEW, *LPCHASENEW;


typedef struct tagCHASENEWLIST
{
	int nDataCount;
	LPCHASENEW lpChasenews;
}CHASENEWLIST, *LPCHASENEWLIST;

typedef struct tagCHASENEWLISTSIMPLE
{
	int nDataCount;
	int *lpMedias;
}CHASENEWLISTSIMPLE, *LPCHASENEWLISTSIMPLE;


/*
history bookmark 等输入参数
*/
typedef struct tagUSERDATAPARAM
{
	int		nChannelID;
	int		nPageNo;
	int		nPageSize;
	int		nLatestDays;
}USERDATAPARAM, *LPUSERDATAPARAM;

typedef struct tagRECOMMENDMEDIA
{
	char			desc[1024];				//影片描述
	float			score;					//评分
	int				nScoreCount;			//
	int				nSetNow;				//当前有效集数
	int				nMediaType;				//当前 media的类型  头文件中有定义
	//LPPOSTERINFOR   lpPosterInfo;			//图片信息
	LPPERSONINFOR	lpPersonInfor;			//个人信息简介
	MEDIAINFO		mediaInfo;
}RECOMMENDMEDIA, *LPRECOMMENDMEDIA;

typedef struct tagALBUMINFO
{
	int			nAlbumId;
	char		szName[64];
	char		szDesc[512];
	URLINFO	poster;
}ALBUMINFO,	*LPALBUMINFO;

typedef struct tagRECOMMENDMEDIALIST
{
	unsigned int	uUpdateTime;			//推荐更新时间
	int				nDataCount;
	LPRECOMMENDMEDIA lpRecommendMedia;
}RECOMMENDMEDIALIST, *LPRECOMMENDMEDIALIST;

typedef struct tagRECOMMENDMEDIAINFO
{
	char			desc[1024];				//影片描述
	POSTERINFOR     posterInfo;				//图片信息
	MEDIAINFO		mediaInfo;
}RECOMMENDMEDIAINFO, *LPRECOMMENDMEDIAINFO;

typedef struct tagRECOMMENDPERSONINFO
{
	POSTERINFOR     posterInfo;				//图片信息
	PERSONINFOR		personInfor;			//个人信息简介
}RECOMMENDPERSONINFO, *LPRECOMMENDPERSONINFO;

typedef struct tagRECOMMENDALBUMINFO
{
	POSTERINFOR     posterInfo;				//图片信息
	ALBUMINFO		albumInfo;				//个人信息简介
}RECOMMENDALBUMINFO, *LPRECOMMENDALBUMINFO;

typedef struct tagRECOMMENDMEDIADATA
{
		int						nMediaType;							//当前 media的类型  头文件中有定义
		LPRECOMMENDMEDIAINFO	lpRecommendMediaInfo;				//电影推荐
		LPRECOMMENDPERSONINFO   lpRecommendPersonInfo;				//个人信息简介
		LPRECOMMENDALBUMINFO	lpRecommendAlbumInfo;				//专辑信息
		LPPRODUCTCODELIST       lpProductCodeList;                  //产品代码列表
}RECOMMENDMEDIADATA, *LPRECOMMENDMEDIADATA;

typedef struct tagRECOMMENDSEARCHKEYWORD
{
	int nDataCount;
	char **lpszSearchKeyWord;
}RECOMMENDSEARCHKEYWORD, *LPRECOMMENDSEARCHKEYWORD;

typedef struct tagRECOMMENDMEDIA2
{
	int						nDataCount;
	LPRECOMMENDMEDIADATA	lpRecommendMedia;
	LPRECOMMENDSEARCHKEYWORD lpHotSearchKeyWord; //热播搜索关键字，如果没有则为NULL
	LPTVDATA                 lpTvData;
}RECOMMENDMEDIA2, *LPRECOMMENDMEDIA2;

typedef struct tagRECOMMENDMEDIALIST2
{
	unsigned int	uUpdateTime;			//推荐更新时间
	int				nDataCount;
	LPRECOMMENDMEDIA2 lpRecommendMedia;
	int				nCoverCount;
	LPURLINFO		lpCoverPosterInfo;				//图片信息
}RECOMMENDMEDIALIST2, *LPRECOMMENDMEDIALIST2;

//登录时服务器返回的信息
typedef struct tagCOMMONINFO
{
	int				nSourceID;				//源id	
	int				arrayBitStream[3];		//各个清晰度的				
	int				nProxy;					//是否使用proxy 1为使用，0不使用
	char			szSourceName[32];		//源名字
	char			szDomain[256];			//domain
	char			szHttpHeader[1024];		//http-header
	char			szDecoder[64];			//decoder

}COMMONINFO, *LPCOMMONINFO;

//代理结构
typedef struct tagPROXYINFO
{
	char			szProxyUrl[128];
	char			szProxyUserName[32];
	char			szProxyPasswd[32];
}PROXYINFO, *LPPROXYINFO;

typedef struct tagDOMAININFOLIST
{
	char			szDeviceCodeMD5[33];			//设备码md5之后的值
	long			lTimestmpOffset;
	int				nBookmark;
	int				nHistory;
	int				nUserLevel;
	int				nSendBehavLogInterval;
	int				nCommonDataCount;
	LPPROXYINFO		lpProxyInfo;
	INITCONNECTION  connectinfo;
	TOKENINFO		securityInfo;
	TOKENINFO		loginInfo;
	LPCOMMONINFO	lpCommonInfo;
}COMMONINFOLIST, *LPCOMMONINFOLIST;

typedef struct tagLIVE
{
	char szLiveName[32];
	char szLiveInfo[32];
	char szLiveDetail[256];

}LIVE, *LPLIVE;

typedef struct tagWEATHERFORECAST
{
	int  nTemp_h;
	int  nTemp_l;
	int  nTemp_c;//
	int  nHumidity;//
	char szProvince[16];
	char szCity[16];
	char szWinddir_d[16];
	char szWinddir_n[16];
	char szSunset[24]; 
	char szPublish[24];
	char szWindlevel_d[16];
	char szDate[24];
	char szCond_d[16];
	char szWindlevel_n[16];
	char szSunrise[24];
	char szCond_n[16];
	char szPm25[8];
	URLINFO	dayPoster;
	URLINFO	nightPoster;
	int  nLiveCount;
	LPLIVE lpLiveList;
}WEATHERFORECAST, *LPWEATHERFORECAST;

typedef struct tagWEATHERFORECASTLIST
{
	int nDataCount;
	LPWEATHERFORECAST lpWeatherforcecast;
	
}WEATHERFORECASTLIST, *LPWEATHERFORECASTLIST;


typedef struct tagWEATHERFORECAST2
{
	int	 nTemp_h;
    int	 nTemp_l;
	int	 nCurrentTemp;
    int  nCurrentHumidity;
	char szCond_d[32];
	char szCond_n[32];
	char szWindlevel_d[32];
    char szWindlevel_n[32];
	char szWinddir_d[32];
    char szWinddir_n[32];
	unsigned long lnDate;
	URLINFO	dayPoster;
	URLINFO	nightPoster;
	URLINFO	dayPosterBig;
	URLINFO	nightPosterBig;
}WEATHERFORECAST2, *LPWEATHERFORECAST2;

typedef struct tagWEATHERFORECASTLIST2
{
	char szCityId[24];
	int nDataCount;
	LPWEATHERFORECAST2 lpWeatherforcecast;
	
}WEATHERFORECASTLIST2, *LPWEATHERFORECASTLIST2;

typedef struct tagVARIETYDETAIL
{
	int nCi;
	char szVideoName[128];
}VARIETYDETAIL, *LPVARIETYDETAIL;

typedef struct tagVARIETYINFO
{
	char szDate[32];
	int nDataCount;
	LPVARIETYDETAIL lpDetail;
}VARIETYINFO, *LPVARIETYINFO;

//
typedef struct tagVARIETY
{
	int nDataCount;
	char szMonth[32];
	LPVARIETYINFO lpInfo;
}VARIETY, *LPVARIETY;

typedef struct tagTRADITIONAL
{
	int nCi;
	char szDate[32];
	char szVideoName[128];
	char szFocus[256];
}TRADITIONAL, *LPTRADITIONAL;

typedef struct tagVARIETYLIST
{
	/*
	当 nStyle 为  YEAR_STYLE 时使用lpVariety指针
	当 nStyle 为  TRADITIONAL_STYLE 时使用lpTraditional指针
	*/
	int nStyle;	
	int nDataCount; 
	LPVARIETY  lpVariety; 
	LPTRADITIONAL lpTraditional;
}VARIETYLIST, *LPVARIETYLIST;


typedef struct tagMEDIAFULLINFO
{
	int				nMediaID;					//影片id
	int				nFlag;
	int				nResolution;				//标记该电影的清晰度
	int				playlength;					//此电影播放时间
	int				nSetNow;					//当前集数
	int				nSetCount;					//影片总集数
	int				nPlayCount;					//播放次数
	float			score;						//评分
	int				nScoreCount;				//
	char			szIssuedate[16];			//上映时间
	char			szLatestissuedate[16];		//最后更新时间
	char			szCategory[32];				//媒体类型
	char			szTags[32];					//电影所属类别
	char			szMediaName[128];			//标题
	char			szDirector[128];			//导演
	char			szActors[512];				//演员
	char			desc[1024];					//影片描述
	char			szLanguage[32];				//语言
	char			szArea[32];					//区域
	int				nIsMultipleSet;				//
	URLINFO			bigPoster;					//大海报图片地址
	URLINFO			smallPoster;				//小海报图片地址
	LPPRODUCTCODELIST lpProductCodeList;        //产品代码列表

}MEDIAFULLINFO, *LPMEDIAFULLINFO;

typedef struct tagMEDIADETAILINFO2
{
	MEDIAFULLINFO 	mediaFullInfo;
	VARIETYLIST		VarietyList;
}MEDIADETAILINFO2, *LPMEDIADETAILINFO2;


typedef struct tagINPUTDETAILINFO2
{
	int 	nMediaId;
	int 	nPageNo;
	int 	nPageSize;
	int 	nOrder;
	int		nPosterPlatform;			//不使用时要填写<=0的值
	char* 	lpszUserBehavData;
}INPUTDETAILINFO2, *LPINPUTDETAILINFO2;

typedef struct tagUPGRADEINFO
{
	int nCanUpgrade;
	int nForce;
	int nWaitingTime; 
	int nPackageSize;
	int nContentSize;
	int nCurrContentSize;
	char szAvailableVersion[64];
	char szAppVersion[34];
	char szExtension[34];
	char szContent[10240];
	char szCurrContent[10240];
	URLINFO	packageInfo;
	URLINFO	releaseNoteInfo;
	URLINFO packageInfo2;
	URLINFO releaseNoteInfo2;

}UPGRADEINFO, *LPUPGRADEINFO;

typedef struct tagFILMREVIEW
{
	int  nScore;				//评论分数如果为-1，表示用户没有发表评分
	int	 nChoice;				//是否是精选 如果是为1，否则为0
	char szUserId[128];			//评论用户的userid
	char szFilmReview[2048];	//评论用户的内容
	char szCreateTime[32];		//评论时间
}FILMREVIEW, *LPFILMREVIEW;

typedef struct tagFILMREVIEWLIST
{
	int nTotalCount;
	int nDataCount;
	LPFILMREVIEW lpFilmReview;
}FILMREVIEWLIST, *LPFILMREVIEWLIST;


typedef struct tagSPECIALSUBJECT
{
	char		szName[64];		//名字
	char		szDesc[1024];		//描述
	int			nChannelId;		//
	int			nMediaCount;		//专题的总电影个数
	URLINFO	poster;
}SPECIALSUBJECT, *LPSPECIALSUBJECT;

typedef struct tagSPECIALSUBJECTLIST
{
	int nDataCount;
	LPSPECIALSUBJECT lpSpecialSubject;
}SPECIALSUBJECTLIST, *LPSPECIALSUBJECTLIST;

typedef struct tagSPECIALSUBJECTMEDIA
{
	int				nMediaTpye;
	LPMEDIAINFO		lpMediaInfo;
	LPPERSONINFOR	lpPersonInfo;
}SPECIALSUBJECTMEDIA, *LPSPECIALSUBJECTMEDIA;

typedef struct tagSPECIALSUBJECTMEDIALIST
{
	int nDataCount;
	LPSPECIALSUBJECTMEDIA lpMediaList;
	POSTERINFOR			  posterInfo;
}SPECIALSUBJECTMEDIALIST, *LPSPECIALSUBJECTMEDIALIST;


typedef struct tagMEDIASOURCEINFO
{
	int		nSource;
	int		nBox;
	int		nMeaiaId;
	int		nCi;
	int		nSetCount;	//总集数
	int		nSetNow;	//当前更新集数
	int 	nIsMultipleSet;
	int		nResolution;
	char	szIssuseDate[32];
	char	szMediaName[128];
	char	szSourceParam[1024];
}MEDIASOURCEINFO, *LPMEDIASOURCEINFO;

struct tagAPPLAYOUTLIST;
typedef struct tagAPPLAYOUT
{
	char		  szAppName[64];
	POSTERINFOR   PosterInfo;
	struct tagAPPLAYOUTLIST *lpSubList;
}APPLAYOUT, *LPAPPLAYOUT;

typedef struct tagAPPLAYOUTLIST
{
	int nDataCount;
	LPAPPLAYOUT  lpLayoutList;
}APPLAYOUTLIST, *LPAPPLAYOUTLIST;

typedef struct tagTVPROGRAMME
{
	int  nStartTime;
	int  nStopTime;
	char szVideName[128];
}TVPROGRAMME, *LPTVPROGRAMME;

typedef struct tagDATETVPROGRAMME
{
	char		  szDate[32];
	int			  nDataCount;
	LPTVPROGRAMME lpProgramme;
}DATETVPROGRAMME, *LPDATETVPROGRAMME;

typedef struct tagTVPROGRAMMEDATE
{
	int			  nChannelId;
	int			  nDataCount;
	LPDATETVPROGRAMME lpDateProgramme;
}TVPROGRAMMEDATE, *LPTVPROGRAMMEDATE;

typedef struct tagTVPROGRAMMELIST
{
	int			  nDataCount;
	LPTVPROGRAMMEDATE lpTVProgramme;
}TVPROGRAMMELIST, *LPTVPROGRAMMELIST;

typedef struct tagTVINFO
{
	int			nChannelId;
	char		szChannelName[64];
	char		szPlayId[32];
	int			nBackGroundColor;
	char		szHeadLetter[4];
	URLINFO		poster;	
	TVPROGRAMME programe;
}TVINFO, *LPTVINFO;

typedef struct tagTVINFOLIST
{
	int			nDataCount;
	LPTVINFO	lpTVInfo;
}TVINFOLIST, *LPTVINFOLIST;

typedef struct tagMEDIAPOSTERINFO
{
	int		nMediaId;
	char	szMediaName[128];
	URLINFO posterInfo;
}MEDIAPOSTERINFO, *LPMEDIAPOSTERINFO;

typedef struct tagCHANNELPOSTERINFO
{
	int					nChannelId;
	int					nDataCount;
	LPMEDIAPOSTERINFO	lpMediaPosterInfo;
}CHANNELPOSTERINFO, *LPCHANNELPOSTERINFO;

typedef struct tagCHANNELPOSTERINFOLIST
{
	int					nVersion;
	int					nDataCount;
	LPCHANNELPOSTERINFO lpChannelPosterInfo;
}CHANNELPOSTERINFOLIST, *LPCHANNELPOSTERINFOLIST;

typedef struct tagDESKTOPPOSTERANIMATOR
{
    int type;
} DESKTOPPOSTERANIMATOR;

typedef struct tagDESKTOPPOSTERDATA
{
    int zIndex;
    char szCDN[256];
    char szName[64];
    char szMd5[34];
    DESKTOPPOSTERANIMATOR animator;
} DESKTOPPOSTERDATA, *LPDESKTOPPOSTERDATA;

typedef struct tagDESKTOPPOSTER
{
    int  nPosterDataCnt;
    LPDESKTOPPOSTERDATA lpPosterDataList;
} DESKTOPPOSTER, *LPDESKTOPPOSTER;

typedef struct tagDESKTOPMEDIA
{
    char szChannelName[32];
    char szMediaName[128];
    int  nChannelMediaCount;
}DESKTOPMEDIA, *LPDESKTOPMEDIA;

typedef struct tagDESKTOPINFO
{
    int nMediaCnt;
    LPDESKTOPMEDIA lpMediaList;
} DESKTOPINFO, *LPDESKTOPINFO;

typedef struct tagDESKTOPDATA
{
    int nVersion;
    DESKTOPINFO info;
    DESKTOPPOSTER bannerPoster;
    DESKTOPPOSTER backPoster;
    DESKTOPPOSTER forePoster;
} DESKTOPDATA, *LPDESKTOPDATA;

enum eErrorMsgCode
{
    eLoginError = 0,            //登陆错误
    ePlayError,                 //播放错误信息
    eOpenUrlError,              //url 打开失败
    ePostUrlError,              //海报地址错误
    eFataError,                 //fata error
    eAssertQuit,
    ePacketError,
    eSeekError,                 //seek errork
    eSetPlayHistoryError,
    eDeletePlayHistoryError,
    eSetBookMarkError,
    eDeleteBookMarkError
};

#ifdef __cplusplus
extern "C"{
#endif

/*
初始化数据
*/
void DK_InitInterface(LPINITCONNECTION lpConnectionInfo);

/*
初始化服务器连接
lpConnectionInfo 如果填写null 是匿名用户登录

返回值定义
*/
int DK_UserLogin(int nDeleteUserInfo);

/*
反初始化连接
*/
void DK_UserLogOut();


/*
获得频道信息列表

返回值定义
大于0为频道数量

nChannelID 为0时查询主频道列表，大于0时查询对应子频道列表
*/
int DK_GetChannelInfoList(int nChannelID, LPCHANNELINFOLIST lpChannelInfoList);

/*
销毁频道信息列表
由于DK_GetChannelInfoList返回的结果是一个链表，使用完之后需要调用该
接口将链表释放掉
*/
void DK_DestroyChannelInfoList(LPCHANNELINFOLIST lpChannelInfoList);


/*
根据频道信息获得频道对应的电影列表信息
lpGetInfo 要求服务器返回的信息

返回值定义
大于0为返回的结果数量
*/
int DK_GetMediaInfoList(LPGETMEDIAINFOLIST lpGetInfo, LPMEDIAINFOLIST lpMediaList, int *lpnRequestId);

/*
销毁媒体信息列表
*/
void DK_DestroyMediaInfoList(LPMEDIAINFOLIST lpMediaList);

/*
根据media id获得媒体详细信息
返回值定义
>0 成功

*/
int DK_GetMediaDetailInfo(int nMediaID, const char* lpszUserBehavData, LPMEDIADETAILINFO lpMediaInfo);

/*
根据media id获得媒体详细信息
返回改media的剧集信息

@param fee的取值：
0 -- 只获取免费视频；
1 -- 获取所有视频；
2 -- 只获取收费视频

*/
int DK_GetMediaDetailInfo2(LPINPUTDETAILINFO2 lpParam, LPMEDIADETAILINFO2 lpMediaInfo, int fee);

/*
销毁DK_GetMediaDetailInfo2占用的内存
*/
void DK_DestroyMediaDetailInfo2(LPMEDIADETAILINFO2 lpMediaInfo);
    
/*
  初始化 LPMEDIAURLINFOLIST 结构体
 */
void DK_InitMediaUrlInfoList(LPMEDIAURLINFOLIST lpMediaUrlInfo);
    
/*
 prepare url
 */
void DK_PrepareMediaUrlInfo(int nMediaID, int nCi, int nSource);


/*
cancel prepare and destroy memory
*/
void DK_CancelPrepareMediaUrlInfo(int nMediaID, int nCi, int nSource);

/*
根据media id获得电影播放的地址
int nMediaID,				media的id号
int nNum,					第几集
lpMediaUrlInfo				所有的url信息
返回值定义

*/
int DK_GetMediaUrlInfo(int nMediaID, int nCi, int nSource, LPMEDIAURLINFOLIST lpMediaUrlInfo);

/*
取得具体的url
nSharpness 定义
#define VIDEO_SHARPNESS_NORMAL		1
#define VIDEO_SHARPNESS_HIGH		2
#define VIDEO_SHARPNESS_SUPER		3

nIndex 想要取得第几个  最大数 URL_MAX_COUNT
*/

int DK_GetMediaUrl(LPMEDIAURLINFOLIST lpMediaUrlInfo, int nSharpness, int nIndex, LPMEDIAURLINFO lpMediaUrl);


/*
调用DK_GetMediaUrl获得url播放后，要调用
DK_DestroyMediaService 进行释放
*/
void DK_DestroyMediaService();


/*
查找影片信息

返回值定义
>0	搜索到的结果数
*/
int DK_SearchMedia(LPSEARCHINFO lpSearchInfo, LPMEDIAINFOLIST lpMediaList, int *lpnRequestId);

/*
电影播放失败，通知服务器更新地址信息
返回值， 

返回值定义
*/
int DK_ReLoadMediaInfo(int nMediaID);


/*
设置播放历史记录

返回值定义
*/
int DK_SetPlayHistory(LPPLAYHISTORY lpPlayHistory, int *lpnServDelID, int *lpnMediaType);

/*
获得播放历史记录

返回值定义
*/
int DK_GetPlayHistory(LPUSERDATAPARAM lpInputData, LPPLAYHISTORYLIST lpPlayHistoryList);

/*
销毁history结构体
*/
void DK_DestroyPlayHistory(LPPLAYHISTORYLIST lpPlayHistoryList);

/*
删除历史记录

返回值定义
*/
int DK_DeletePlayHistory(LPPLAYHISTORY lpPlayHistory);

/*
设置收藏夹

返回值定义
*/
int DK_SetBookMark(LPBOOKMARK lpBookMark);

/*
获得收藏夹内容
返回值定义
*/
int DK_GetBookMarksDetails(LPUSERDATAPARAM lpInputData, LPBOOKMARKLIST lpBookMarks);

/*
删除bookmark返回的结果
*/
void DK_DestroyBookMarksDetails(LPBOOKMARKLIST lpBookMarks);

/*
获得简单的bookmarks
返回值 记录的个数
*/
int DK_GetBookMarksSimple(LPUSERDATAPARAM lpInputData, int **lppMedias);

/*
销毁DK_GetBookMarksSimple返回数据占用的内存
*/
void DK_DestroyBookMarksSimple(int *lpMedias);

/*
nIndex -1 删除所有
删除收藏夹内容
返回值定义
*/
int DK_DeleteBookMark(int nMediaID);

/*批量删除*/
int DK_DeleteBookMark2(int *lpnMediaIds, int nMediaIdCount);

/*
获得推荐内容
返回值定义
大于0返回的数量
只有当nMediaID 等于-1时，uUpdateTime才会生效
uUpdateTime填写0时，无论是否时间超时，服务器都返回推荐信息，
*/
int DK_GetRecommendMedia(int nMediaID, unsigned int	uUpdateTime, LPRECOMMENDMEDIALIST lpRecommendMediaList);


/*
平台号为PLATFORM_ANDROID_AMLOGIC时调用该接口
*/
int DK_GetRecommendMedia2(int nMediaID, unsigned int uUpdateTime, LPRECOMMENDMEDIALIST2 lpRecommendMediaList);

/*
获得推荐内容
返回值定义
大于0返回的数量
*/
int DK_GetUserBasedRecommendMedia(int nMediaID, LPRECOMMENDMEDIALIST lpRecommendMediaList);

/*
销毁推荐内容
返回值定义
*/
void DK_DestroyRecommendMedia(LPRECOMMENDMEDIALIST lpRecommendMediaList);

void DK_DestroyRecommendMedia2(LPRECOMMENDMEDIALIST2 lpRecommendMediaList);
/*
获得指定频道组的media信息
返回值定义
*/
//LPGETMEDIAINFOLIST
int DK_GetFilterMediaInfo(LPGETMEDIAINFOLIST lpGetInfo, LPMEDIAINFOLIST lpMediaList, int *lpnRequestId);

/*
获得指定频道组的media信息. @param fee的取值：
0 -- 只获取免费视频；等同于调用DK_GetFilterMediaInfo接口
1 -- 获取所有视频；
2 -- 只获取收费视频
 */
int DK_GetFilterMediaInfo2(LPGETMEDIAINFOLIST lpGetInfo, LPMEDIAINFOLIST lpMediaList, int *lpnRequestId, int fee);

/*
获得source name通过 sourceid 1为找到，0没有找到
*/
int DK_GetSourceNameByID(int nSourceID, char *lpszSourceName, int nNameBufferLen);


/*
获得码流信息通过 sourceid NULL 为没找到, 返回的结构为[100,200,300]
*/
int* DK_GetSourceBitStream(int nSourceID);


/*
获得http header通过 sourceid 1为找到，0没有找到
*/
int DK_GetHttpHeaderByID(int nSourceID, char *lpszSourceName, int nNameBufferLen);


/*
获得m3u8下载路径
*/
int DK_GetDownLoadM3U8Path(char *lpszDownloadPath, int nBufferLen);


/*
获得服务器能够存储的最大 书签 
*/
int DK_GetMaxBookMark();

/*
获得服务器能够存储的最大 历史记录
*/
int DK_GetMaxHistory();

int	DK_GetMediaBatch(LPGETMEDIAINFOLIST lpGetInfo, LPMEDIAINFOLIST lpMediaList, int *lpnRequestId);
    
/*
     天气预报接口
     int nLatestDays 返回值的天数
     char* lpszPostCode 不写请填写 NULL
     LPWEATHERFORECASTLIST lpRetData 返回数据
     返回值为记录条数
*/
int DK_GetWeatherforecast(int nLatestDays, char* lpszPostCode, LPWEATHERFORECASTLIST lpRetData);

/*
销毁天气预报返回的结果
*/
void DK_DestroyWeatherforecast(LPWEATHERFORECASTLIST lpRetData);

/*
天气预报接口
*/
int DK_GetWeatherforecast2(int nLatestDays, char* lpszCityId, int nVer, LPWEATHERFORECASTLIST2 lpRetData);

/*
销毁天气预报结构接口
*/
void DK_DestroyWeatherforecast2(LPWEATHERFORECASTLIST2 lpRetData);
/*
获得娱乐节目信息
*/
int DK_GetVarietyMediaInfo(int nMediaID, int nYear, LPVARIETYLIST lpVarietyList);

/*
*/
int DK_GetVarietyMediaInfo2(LPGETMEDIAVARIETY lpGetInfo, LPVARIETYLIST lpVarietyList);

/*
销毁娱乐节目返回的信息
*/
void DK_DestroyVarietyMediaInfo(LPVARIETYLIST lpVarietyList);

/*
返回所有相关联的影片
*/
int DK_GetMediaByCelebrity(LPGETMEDIABYCELEBRITY lpGetInfo, LPMEDIAINFOLIST lpMediaList);

/*
获得个人信息
*/
int DK_GetPersonInfor(int nCelebrityId, LPPERSONINFOR lpPersonInfor);

/*初始化日志记录*/
void DK_InitLogConfig();

/*设置当前页面信息*/
void DK_SetCurPageInfo(int page, int wParam, int lParam);

int DK_SetCurPageInfo2(int page, int wParam, int lParam, const char* lpszExternal);

/*把错误信息写到log中，等待上传服务器*/
//void SetErrorMsg(int nErrorCode, int nStatus, int nParam, const char *lpszStrText, const char* lpszSysLog);

/*返回登录服务器时的服务器时间，距离1970年的时间， login之后才有效，错误为返回0*/
unsigned long DK_GetLoginServerTime();

/*是否是大陆用户， 如果是返回1， 不是返回0， 需要在登录之后调用*/
int DK_IsMainlandUser();

/*追新接口, 添加追新的mediaid*/
int DK_SetChasenewMedia(int nMediaID, int nCi, int *lpnRemainCount);

int DK_SetChasenewMedia2(LPINPUTCHASENEW lpInputParam, int *lpnRemainCount);


/*追新接口, 获得追新的media list*/
int DK_GetChasenewMediaList(LPUSERDATAPARAM lpInputData, LPCHASENEWLIST lpUpdateList, LPCHASENEWLIST lpNotUpdateList);

/*
追新接口, 销毁从DK_GetChasenewMediaList获得的列表
*/
void DK_DestroyChasenewMediaList(LPCHASENEWLIST lpChasenowList);

/*追新接口, 删除已经添加的追新media*/
int DK_DeleteChasenewMedia(int nMediaID);

//计算字符串的MD5值
void MD5String(char const* str, char *output);

//计算文件MD5值
void MD5File(char *filename, char *output);

//计算二进制数据的MD5值
void MD5BinData(const unsigned char *lpszData, int nDataLen, char *output);

/*aes 加密*/
int aes_encrypt(char *lpszInput, char *lpszOutput, const unsigned char* lpszKey);

/*aes 解密*/
int aes_decrypt(char *lpszInput, int nInputLen, char *lpszkey, char *lpszOutput);

/*base64编码*/
int Base64Encode(unsigned char* input, int inLen, char* output, int outLen);

/*base64解码*/
int Base64Decode(char *lpszInBuf, int nInBuf, char *lpszOutMsg, int nOutBuf);

/*
版本更新接口
返回值 1为成功，其他值为失败
*/
int DK_GetUpgradeInfo(int nUpgradeType, int nNoteLength, LPUPGRADEINFO lpUpgradeInfo);

/*
上传版本更新状态
返回值 1为成功，其他值为失败
*/
int DK_SetUpgradeInfo();

/*
获得排行榜信息
*/
int DK_GetRankingListInfor(LPCHANNELINFOLIST lpRankingList);

/*
获得排行榜影片信息
*/
int DK_GetRankingListMediaInfor(LPGETMEDIAINFOLIST lpGetInfo, LPRANKINGLIST lpRankinglist);

/*
销毁排行榜
*/
void DK_DestroyRankingList(LPRANKINGLIST lpRankinglist);

/*
nChannelId 填写0时获得主页的轮播，填写-1时获得所有频道轮播
获得轮播影片接口
*/
int DK_GetBannerMedia(int nChannelId, LPRECOMMENDMEDIA2 lpBannerList);

/*
销毁轮播占用的内存
*/
void DK_DestroyBannerMedia(LPRECOMMENDMEDIA2 lpRecommendMediaList);
/*
获得频道影片推荐
*/
int DK_GetChannelRecommendMedia(LPGETMEDIAINFOLIST lpGetInfo, LPCHANNELRECOMMENDMEDIALIST lpMediaList);

/*
销毁频道影片推荐
*/
void DK_DestroyChannelRecommendMedia(LPCHANNELRECOMMENDMEDIALIST lpMediaList);

/*
上传影评
评分
想到获得当前电影的评分请从
DK_GetMediaDetailInfo或者
DK_GetMediaInfoList或者
DK_SearchMedia或者
DK_GetFilterMediaInfo等接口的信息中获得
nScore 的分数为 0<=nScore<=10
如果没有评论内容则lpFilmReview 设置为 NULL
如果只有评论，nScore 填写-1 ，服务器则不统计该分数
*/
int DK_SetFilmReview(int nMediaId, int nScore, const char* lpFilmReview);

/*
下载影评
*/
int DK_GetFilmReview(int nMediaId, int nPageSize, int nPageNo, LPFILMREVIEWLIST lpFilmReviewList);

/*
销毁影评
*/
int DK_DestroyFilmReview(LPFILMREVIEWLIST lpFilmReviewList);

/*
获得专题列表
*/
int DK_GetSpecialSubjectList(LPSPECIALSUBJECTLIST lpSpecialSubjectList);

/*
销毁专题列表
*/
void DK_DestroySpecialSubjectList(LPSPECIALSUBJECTLIST lpSpecialSubjectList);

/*
获得专题影片信息
*/
int DK_GetSpecialSubjectMedia(int nSpecialSubjectId, LPSPECIALSUBJECTMEDIALIST lpMediaList);

/*
获得专题影片信息， 上传用户行为信息
*/
int DK_GetSpecialSubjectMedia2(int nSpecialSubjectId, const char* lpszUserBehavData, LPSPECIALSUBJECTMEDIALIST lpMediaList);

/*
销毁专题影片信息
*/
void DK_DestroySpecialSubjectMedia(LPSPECIALSUBJECTMEDIALIST lpMediaList);

/*
获得密钥
*/
int DK_GenerateDeviceSecurity(LPTOKENINFO lpTokenInfo);

/*
nWaitLogin 为 1时等待login接口返回，为0时，如果没有login其他接口则直接return
nWaitTimeOunt 为如果没有调用login接口，其他接口等待的时间，单位为秒，0为永远等待。
*/
void DK_SetWaitLoginParam(int nWaitLogin, int nWaitTimeOunt);

/*
上传文件接口
*/
int DK_UploadFile(const char* lpszFileName);
int DK_UploadFile2(const char* lpszFileName, const char* param);

/*
获得当前用户级别，需要在userlogin调用成功之后调用
*/
int DK_GetUserLevel();

/*
测速接口
*/
int DK_GetNetworkSpeedUrl(LPNETWORKSPEEDURLLIST lpNetWorkSpeedUrl);

/*
释放测速接口内存
*/
void DK_DestroyNetworkSpeedUrl(LPNETWORKSPEEDURLLIST lpNetWorkSpeedUrl);

/*
通过palyurl获得相关信息
*/
int DK_GetMediaInfoByPlayUrl(const char* lpszPlayUrl, int nSource, LPMEDIASOURCEINFO lpMediaSourceInfo, const char *lpszUserBehavData);

/*
销毁相关内存
*/
void DK_DestroyMediaInfoByPlayUrl(LPMEDIASOURCEINFO lpMediaSourceInfo);

/*
打开clientsdk log输出
*/
void DK_EnableClientSDKLog();

/*
关闭clientsdk log输出
*/
void DK_DisableClientSDKLog();

/*
删除用户信息，返回1为操作成功，其他为错误。
*/
int DK_DeleteUserInfo();

/*
刷新服务器地址， lpszServerAddress为新指定的服务器地址。
*/
void DK_RefreshServerAddress(const char *lpszServerAddress);

/*
刷新升级服务器地址， lpszServerAddress为新指定的服务器地址。
*/
void DK_RefreshUpgradeAddress(const char *lpszServerAddress);

/*
刷新日志服务器地址， lpszServerAddress为新指定的服务器地址。
*/
void DK_RefreshUploadLogAddress(const char *lpszServerAddress);

/*
刷新获取天气城市信息的服务器地址，lpszServerAddress为新制定的服务器地址。
*/
void DK_RefreshWeatherCityURLAddress(const char *lpszServerAddress);

/*
应用layout配置
*/
int DK_AppLayout(LPAPPLAYOUTLIST lpLayoutList);

/*
释放DK_AppLayout 使用的内存
*/
void DK_DestroyAppLayout(LPAPPLAYOUTLIST lpLayoutList);

/*
获得天气预报支持的地区信息
*/
int DK_GetWeatherCitysInfo(const char* lpszMd5, char** lppszWeatherInfo);

/*
释放天气预报地区信息占用的内存
*/
void DK_DestroyWeatherCitysInfo(char* lpszWeatherInfo);


/*
获得本机状态，用32位表示状态，
第一位 0 不在黑名单列表， 1 在黑名单列表
*/
int DK_GetDeviceStatus(unsigned int *lpnStatus);


/*
上传用户播放信息
*/
int DK_SetPlayInfo(const char* lpszPlayInfo);

/*
上传自定义信息
*/
int DK_ReportMessage(const char* lpszMessage);

/*获得电视节目单*/
int DK_GetTVProgramme(LPGETMEDIAINFOLIST lpGetInfo, LPTVPROGRAMMELIST lpTVProgrammeList, const char *lpszBehavLog);

/*释放节目单占用的内存*/
void DK_DestroyTVProgramme(LPTVPROGRAMMELIST lpTVProgrammeList);

/*
获得推荐的电视台信息
*/
int DK_GetTVChannelRecommend(LPGETMEDIAINFOLIST lpGetInfo, LPTVINFOLIST lpTVList);

/*
销毁电台信息
*/
void DK_DestroyTVChannelRecommend(LPTVINFOLIST lpTVList);


/*
获取静态推荐数据
*/
int DK_GetRecommendMedia3(unsigned int uUpdateTime, LPRECOMMENDMEDIALIST2 lpRecommendMediaList);

/*
获取静态排行榜数据
*/
int DK_GetRankingListMediaInfor2(int nVer, LPRANKINGLIST lpRankinglist);

/*
获得每个分类前24个media信息
*/

int DK_GetChannelPosterInfo(int nVer, LPCHANNELPOSTERINFOLIST lpPosterList);

/*
销毁LPCHANNELPOSTERINFOLIST 中的数据
*/
void DK_DestroyPosterInfo(LPCHANNELPOSTERINFOLIST lpPosterList);

/*
获得静态数据
*/
int DK_GetChannelInfoList2(int nVer, LPCHANNELINFOLIST lpChannelInfoList);

/*
手机上传imei信息，用于统计活跃用户数，启动次数等。
*/
int DK_UploadIMEIBootInfo(const char *lpszIMEI, const char *lpszUniqueIndentify, const char* lpszDeviceName, const char *lpszNetWorkType);

/*
获得TV桌面上显示的在线影视的海报和media信息
返回>0表示成功
*/
int DK_GetDesktopInfo(int nVer, LPDESKTOPDATA lpDesktopData);

void DK_DestroyDesktopInfo(LPDESKTOPDATA lpDesktopData);

/*
tv 专题 layout
*/
int DK_GetSpecialSubjectList2(int nPageSize, int nPageNo, LPSPECIALSUBJECTLIST lpSpecialSubjectList);

#ifdef __cplusplus
}
#endif

#endif
