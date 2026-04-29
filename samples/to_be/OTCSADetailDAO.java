package com.pan.som.dao.sa;

import com.pan.som.common.dto.erp.EARIfReceiptBalanceVDTO;
import com.pan.som.common.dto.erp.EARVendorBankAccountVDTO;
import com.pan.som.common.dto.sa.*;
import com.pan.som.common.dto.salesOpportunity.SCBVslVoyMDTO;
import com.pan.som.common.dto.standardInfo.CCDTaxDetailDTO;
import com.pan.som.common.utility.CommonDao;
import com.pan.som.common.utility.Formatter;
import com.pan.som.common.utility.UserBean;
import com.pan.som.common.vo.sa.OTCSaCbDetailVO;
import com.pan.som.common.vo.sa.OTCSaDetailVO;
import com.pan.som.common.vo.sa.OTCSaHeadVO;
import com.pan.som.common.vo.standardInfo.CCDTrsactTypeMVO;
import com.pan.som.dao.standardInfo.CCDTrsactTypeMDAO;
import com.pan.som.dao.standardInfo.CCDVslCodeMDAO;
import kr.co.takeit.dao.UxbDAO;
import kr.co.takeit.exception.UxbBizException;
import kr.co.takeit.session.UserInfo;
import kr.co.takeit.session.user.UserDelegation;
import kr.co.takeit.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author yoonsook
 */
@Slf4j
@RequiredArgsConstructor
@Repository
public class OTCSADetailDAO {

    private final CommonDao commonDao;
    private final UxbDAO uxbDAO;
    private final CCDTrsactTypeMDAO tdao;
    private final OTCSAHeadDAO saDao;
    private final CCDVslCodeMDAO vslDao;

    /**
     * <p>
     * 설명: sa Detail 내역을 등록하는 메소드이다.
     *
     * @param saDetailVO OTCSaDetailVO : Sa Head정보
     * @param saDetailVO
     * @return msgCode String: Sa Detail 테이블에 저장할 시 발생하는 메소드를 리턴한다.
     * <p>
     * saDetail Insert 실행하다 발생하는 모든 Exception을 처리한다
     * @return
     */
    public String saDetailInsert(OTCSaDetailVO saDetailVO) throws Exception {
        UserDelegation userInfo = UserInfo.getUserInfo();

        String result = "";
        if (saDetailVO != null) {
            StringBuilder sb = new StringBuilder();
            // Query 가져오기
            sb.append("SELECT V.* FROM OTC_SA_DETAIL V ");
            saDetailVO.setSys_cre_user_id(userInfo.getUserId());
            saDetailVO.setSys_upd_user_id(userInfo.getUserId());
            saDetailVO.setSys_cre_date(new Date(System.currentTimeMillis()));
            saDetailVO.setSys_upd_date(new Date(System.currentTimeMillis()));
            commonDao.setObject(saDetailVO, sb.toString(), 1);
            result = "SUC-0600";
        }
        return result;
    }

    /**
     * <p>
     * 설명: sa Detail내역을 등록하는 메소드이다.
     *
     * @param saDetailVO OTCSaDetailVO : Sa Head정보
     * @param saDetailVO
     * @return msgCode String: Sa Detail 테이블에 저장할 시 발생하는 메소드를 리턴한다.
     * <p>
     * saDetail Insert 실행하다 발생하는 모든 Exception을 처리한다
     * @return
     */
    public String saDetailUpdate(OTCSaDetailVO saDetailVO) throws Exception {
        UserDelegation userInfo = UserInfo.getUserInfo();

        String result = "";
        if (saDetailVO != null) {
            StringBuilder sb = new StringBuilder();
            // Query 가져오기
            sb.append("		SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
            sb.append("	          FROM OTC_SA_DETAIL V    ");
            sb.append(" WHERE V.SA_NO = " + Formatter.nullLong(StringUtil.nvl(saDetailVO.getSa_no(), "0")) + " ");
            sb.append(" AND V.SA_SEQ = " + Formatter.nullLong(StringUtil.nvl(saDetailVO.getSa_seq(), "0")) + " ");
            saDetailVO.setSys_upd_user_id(userInfo.getUserId());
            saDetailVO.setSys_upd_date(new Date(System.currentTimeMillis()));
            commonDao.setObject(saDetailVO, sb.toString(), 2);
            result = "SUC-0600";
        }
        return result;
    }

    /**
     * <p>
     * 설명:sa seq max번호 내역을 조회하는 메소드이다.
     *
     * @param saNo no
     * @return sa seq max번호
     */
    public Long saSeqMaxNoSelect(Long saNo) {
        Long result = null;
        long seq = 0;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saSeqMaxNoSelect", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                seq = StringUtil.toLong((String) map.get("max_seq"), 0L);
            }
        }
        if (seq == 0) {
            result = 1L;
        } else {
            result = seq;
        }
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail 내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     */
    public OTCSaDetailVO saDetailSelect(Long saNo, Long saSeq) throws Exception {
        OTCSaDetailVO result = null;
        StringBuilder sb = new StringBuilder();
        // Query 가져오기
        sb.append("		SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
        sb.append("	          FROM OTC_SA_DETAIL V    ");
        sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");
        sb.append(" AND SA_SEQ = " + saSeq.longValue() + " ");
        result = (OTCSaDetailVO) commonDao.getObject(OTCSaDetailVO.class, sb.toString());
        return result;
    }

    /**
     * <p>
     * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 Charterers A/C 정보를 읽어온다
     * chtInOutCOde : O, T, R
     */
    public Collection saChartererACSearch(Long saNo, Long voyNo) {
        Collection result = null;
        String legal = "";
        String legalVsl = "";
        // **************************** Charterers' A/C 가져오기 시작
        // **************************** //
        String detailSql = "";
        detailSql = " SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ";
        detailSql = detailSql + "  FROM OTC_SA_DETAIL V    ";
        //sb.append(" AND  V.trsact_code IN ('J001', 'J002','J003','J004','J005','J006','J007','J006','J007','J008','J009') ORDER BY V.SA_SEQ ");  // 신규 거래유형(J008,J009) 추가 20150422 hijang

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);
        paramMap.put("voyNo", voyNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saChartererACSearch", paramMap);
        result = new ArrayList<>();
        Collection chts = new ArrayList<>();
        OTCSaChartererACDTO chtDTO = null;
        String trsact = "";
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                chtDTO = new OTCSaChartererACDTO();
                trsact = StringUtil.nvl(map.get("TRSACT_CODE"), "");
                if ("J001".equals(trsact)) {
                    chtDTO.setItem_name("ON/OFF-HIRE SVY");
                } else if ("J002".equals(trsact)) {
                    chtDTO.setItem_name("PORT CHARGE");
                } else if ("J003".equals(trsact)) {
                    chtDTO.setItem_name("CARGO CHARGE");
                } else if ("J004".equals(trsact)) {
                    chtDTO.setItem_name("BUNKER CHARGE");
                } else if ("J005".equals(trsact)) {
                    chtDTO.setItem_name("CLAIM");
                } else if ("J006".equals(trsact)) {
                    chtDTO.setItem_name("AP");
                } else if ("J007".equals(trsact)) {
                    chtDTO.setItem_name("CARGO CHARTERERS'A/C(AP)");
                } else if ("J008".equals(trsact)) {
                    //chtDTO.setItem_name("BUNKER CHARGE(FO)");
                    chtDTO.setItem_name("FO SUPPLY BY OWNER");    //명칭변경 20180206 GYJ
                } else if ("J009".equals(trsact)) {
                    //chtDTO.setItem_name("BUNKER CHARGE(DO)");
                    chtDTO.setItem_name("DO SUPPLY BY OWNER");    //명칭변경 20180206 GYJ
                } else if ("J010".equals(trsact)) {        // 신규 거래유형(K010) 추가 (20170714 HIJANG)
                    chtDTO.setItem_name("PREPAYMENT(Ballast)");
                } else if ("J011".equals(trsact)) {        // 신규 거래유형(J011) 추가 (240206)
                    chtDTO.setItem_name("PORT CHARGE(EU ETS)");
                } else if ("J012".equals(trsact)) {        // 신규 거래유형(J012) 추가 (250212)
                    chtDTO.setItem_name("PORT CHARGE(FUEL EU)");
                }
                chtDTO.setSa_no(Formatter.nullDouble(StringUtil.nvl(map.get("SA_NO"), "0")));
                chtDTO.setItem(trsact);
                chtDTO.setAmount_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                chtDTO.setAmount_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                chtDTO.setRemark(StringUtil.nvl(map.get("REMARK")));
                chtDTO.setVessel(StringUtil.nvl(map.get("VSL_CODE")));
                chtDTO.setVoyage(Formatter.nullLong(StringUtil.nvl(map.get("VOY_NO"), "0")));
                chtDTO.setAccount(StringUtil.nvl(map.get("CGO_ACC_CODE")));
                chtDTO.setVat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                chtDTO.setTax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                chtDTO.setOrg_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                chtDTO.setVat_amount_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0")));
                chtDTO.setVat_amount_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0")));
                chtDTO.setTax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                chtDTO.setBnk_prc(Formatter.nullDouble(StringUtil.nvl(map.get("BNK_PRC"), "0")));    // 신규 거래유형(I074,I075,J008,J009) 추가 20150422 hijang (BNK_PRC, BNK_QTY 추가)
                chtDTO.setBnk_qty(Formatter.nullDouble(StringUtil.nvl(map.get("BNK_QTY"), "0")));
                chtDTO.setBnk_type(StringUtil.nvl(map.get("BNK_TYPE")));    // BUNKER TYPE 추가 (hijang 20200924)
                chts.add(chtDTO);
            }
        }
        result.add(chts);
        // **************************** Charterers' A/C 가져오기 종료
        // **************************** //
        // **************************** Charterers' A/C Item 가져오기 시작
        // **************************** //
        StringBuilder sb1 = new StringBuilder();
        //sb1.append(" AND  V.trsact_code IN ('K001','K002','K003','K004','K005','K006') ORDER BY V.SA_SEQ ");
        // 일반관리비 은행수수료('630601') -> 고정비 은행수수료('540203') 으로 계정 변경 관련.... 신규 거래유형(K007) 추가 (20160906 HIJANG)

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", saNo);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saChartererACSearch1", paramMap1);
        OTCSaChatererACSubDTO subDTO = new OTCSaChatererACSubDTO();
        String trsactCd = "";
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap1) {
                trsactCd = StringUtil.nvl(map.get("TRSACT_CODE"), "");
                if ("K001".equals(trsactCd)) {
                    subDTO.setMiss_gain_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                } else if ("K002".equals(trsactCd)) {
                    subDTO.setMiss_loss_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                } else if ("K003".equals(trsactCd)) {
                    subDTO.setBank_carge_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                    // 일반관리비 은행수수료('630601') -> 고정비 은행수수료('540203') 으로 계정 변경 관련.... 신규 거래유형(K007) 추가 (20160906 HIJANG)
                } else if ("K007".equals(trsactCd)) {
                    subDTO.setBank_carge_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                } else if ("K004".equals(trsactCd)) {
                    subDTO.setArgument_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                } else if ("K005".equals(trsactCd)) {
                    legal = "Y";
                    subDTO.setLegal_claim_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                    subDTO.setLegal_claim_voy(Formatter.nullLong(StringUtil.nvl(map.get("VOY_NO"), "0")));
                } else if ("K006".equals(trsactCd)) {
                    legalVsl = "Y";
                    subDTO.setLegal_claim_vsl_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                    subDTO.setLegal_claim_vsl_voy(Formatter.nullLong(StringUtil.nvl(map.get("VOY_NO"), "0")));
                }
            }
        }
        if (!"Y".equals(legal))
            subDTO.setLegal_claim_voy(voyNo);
        if (!"Y".equals(legalVsl))
            subDTO.setLegal_claim_vsl_voy(voyNo);
        result.add(subDTO);
        // **************************** Charterers' A/C Item 가져오기 종료
        // **************************** //
        return result;
    }

    /**
     * <p>
     * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 Withholding Tax 정보를 읽어온다
     * chtInOutCOde : O, T, R
     */
    public OTCSaWithholdingTaxDTO saWithholdingSearch(Long saNo) {
        OTCSaWithholdingTaxDTO result = new OTCSaWithholdingTaxDTO();
        // **************************** Vessel Flag 가져오기 시작
        // **************************** //

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saWithholdingSearch", paramMap);
        String wthFlag = "";
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                wthFlag = StringUtil.nvl(map.get("WTH_FLAG"), "");
                result.setSa_no(Formatter.nullDouble(StringUtil.nvl(map.get("SA_NO"), "0")));
                result.setWth_flag(StringUtil.nvl(map.get("WTH_FLAG"), ""));
                result.setVsl_nat_code(StringUtil.nvl(map.get("WTH_NAT_CODE"), ""));
                result.setOnHire_balance(Formatter.nullDouble(StringUtil.nvl(map.get("WTH_HIRE_BAL_AMT"), "0")));
                if (StringUtil.toInt(map.get("WTH_HIRE_BAL_REDUCE_RATE"), 0) == 0) {
                    result.setReduce_amt(0.0);
                } else {
                    result.setReduce_amt(Double.valueOf(1 - Formatter.nullDouble(StringUtil.nvl(map.get("WTH_HIRE_BAL_REDUCE_RATE"), "0"))));
                }
                result.setVsl_nat_name(StringUtil.nvl(map.get("WTH_NAT_NAME"), ""));
            }
        }
        // **************************** Vessel Flag 가져오기 종료
        // **************************** //
        // **************************** Withholding Tax 가져오기 시작
        // **************************** //
        if ("Y".equals(wthFlag)) {
            StringBuilder sb1 = new StringBuilder();
            String detailSql = "";
            detailSql = " SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ";
            detailSql = detailSql + "  FROM OTC_SA_DETAIL V    ";

            Map<String, Object> paramMap1 = new HashMap<>();
            paramMap1.put("saNo", saNo);

            List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saWithholdingSearch1", paramMap1);
            String trsactCd = "";
            if (listMap1 != null && !listMap1.isEmpty()) {
                for (Map<String, Object> map : listMap1) {
                    trsactCd = StringUtil.nvl(map.get("TRSACT_CODE"), "");
                    if ("M001".equals(trsactCd)) {
                        result.setWith_income_check("Y");
                        result.setIncome_tax_rate(Formatter.nullDouble(StringUtil.nvl(map.get("SA_RATE"), "0")));
                        result.setIncome_tax_code(StringUtil.nvl(map.get("TAX_CODE_ID")));
                        if (StringUtil.toInt(map.get("SA_RATE"), 0) != 0) {
                            result.setIncome_tax_base_usd(Formatter.round(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")) / (Formatter.nullDouble(StringUtil.nvl(map.get("SA_RATE"), "0")) / 100), 2));
                        }
                        result.setIncome_tax_amt_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                        result.setIncome_tax_amt_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                        result.setIncome_tax_code_name("");
                    } else if ("M002".equals(trsactCd)) {
                        result.setWith_inhabit_check("Y");
                        result.setInhabit_tax_rate(Formatter.nullDouble(StringUtil.nvl(map.get("SA_RATE"), "0")));
                        result.setInhabit_tax_code(StringUtil.nvl(map.get("TAX_CODE_ID")));
                        if (StringUtil.toInt(map.get("SA_RATE"), 0) != 0) {
                            result.setInhabit_tax_base_usd(Formatter.round(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")) / (Formatter.nullDouble(StringUtil.nvl(map.get("SA_RATE"), "0")) / 100), 2));
                        }
                        result.setInhabit_tax_amt_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                        result.setInhabit_tax_amt_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                        result.setInhabit_tax_code_name("");
                    }
                } // rs while
            } // rs while
            // **************************** Withholding Tax 가져오기 종료
            // **************************** //
            // **************************** Tax Base Calculation 가져오기 시작
            // **************************** //
            StringBuilder sb2 = new StringBuilder();
            //sb2.append(" 	        AND TRSACT_CODE = 'A001' ");
            //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
            //sb2.append(" 			   AND TRSACT_CODE = 'A002' ");
            //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
            //sb2.append(" 			   AND TRSACT_CODE = 'G001' ");
            //sb2.append(" 			   AND TRSACT_CODE = 'G002' ");
            //sb2.append(" 			   AND TRSACT_CODE = 'H001' ");
            //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
            //sb2.append(" 			   AND TRSACT_CODE = 'H002' ");
            //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
            //sb2.append(" 		      AND TRSACT_CODE  IN ('I003', 'I004') ");
            //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
            //sb2.append(" 			   AND TRSACT_CODE = 'I005' ");
            //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218

            Map<String, Object> paramMap2 = new HashMap<>();
            paramMap2.put("saNo", saNo);

            List<Map<String, Object>> listMap2 = uxbDAO.select("OTCSADetail.saWithholdingSearch2", paramMap2);
            if (listMap2 != null && !listMap2.isEmpty()) {
                for (Map<String, Object> map : listMap2) {
                    result.setOnHire(Formatter.nullDouble(StringUtil.nvl(map.get("ON_HIRE"), "0")));
                    result.setOnHire_add_comm(Formatter.nullDouble(StringUtil.nvl(map.get("ON_HIRE_ADD"), "0")));
                    result.setBonus(Formatter.nullDouble(StringUtil.nvl(map.get("BONUS"), "0")));
                    result.setBonus_add_comm(Formatter.nullDouble(StringUtil.nvl(map.get("BONUS_ADD"), "0")));
                    result.setOffHire(Formatter.nullDouble(StringUtil.nvl(map.get("OFF_HIRE"), "0")));
                    result.setOffHire_add_comm(Formatter.nullDouble(StringUtil.nvl(map.get("OFF_HIRE_ADD"), "0")));
                    result.setSpeed_Claim(Formatter.nullDouble(StringUtil.nvl(map.get("SPEED"), "0")));
                    result.setSpeed_Claim_comm(Formatter.nullDouble(StringUtil.nvl(map.get("SPEED_CLAIM_ADD"), "0")));
                }
            }
            // **************************** Tax Base Calculation 가져오기 종료
            // **************************** //
        } else {
            result.setWth_flag("N");
            result.setResultMsg("UCG-2010"); // 원천징수 대상 국가가 아닙니다.
        }
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public Collection saOwnerACSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo) {
        Collection result = null;
        boolean brok_check = false;
        // **************************** Brokerage 가져오기 시작
        // **************************** //
        StringBuilder sb = new StringBuilder();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saOwnerACSearch", paramMap);
        OTCSaBrokerageDTO brokDTO = new OTCSaBrokerageDTO();
        result = new ArrayList<>();
        int row = 0;
        String brChk = "";
        String brok11 = "";
        String brok12 = "";
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                brChk = "Y";
                if (row == 0) {
                    brok11 = StringUtil.nvl(map.get("BROK_ACC_CODE"), "");
                    brokDTO.setBroker(StringUtil.nvl(map.get("BROK_ACC_CODE")));
                    brokDTO.setBroker_name(StringUtil.nvl(map.get("BROK_NAME"), ""));
                    brokDTO.setBrokerage_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                    brokDTO.setBrokerage_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                    brokDTO.setComm(Formatter.nullDouble(StringUtil.nvl(map.get("SA_RATE"), "0")));
                    brokDTO.setRemark(StringUtil.nvl(map.get("REMARK")));
                    brokDTO.setBrok_reserve_flag(StringUtil.nvl(map.get("BROK_RESERV_FLAG"), ""));
                    brokDTO.setBank_acc_id(StringUtil.nvl(map.get("BANK_ACC_ID"), ""));
                    brokDTO.setBank_acc_desc(StringUtil.nvl(map.get("BANK_ACC_DESC"), ""));
                } else if (row == 1) {
                    brok12 = StringUtil.nvl(map.get("BROK_ACC_CODE"), "");
                    brokDTO.setBroker2(StringUtil.nvl(map.get("BROK_ACC_CODE")));
                    brokDTO.setBroker_name2(StringUtil.nvl(map.get("BROK_NAME"), ""));
                    brokDTO.setBrokerage_krw2(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                    brokDTO.setBrokerage_usd2(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                    brokDTO.setComm2(Formatter.nullDouble(StringUtil.nvl(map.get("SA_RATE"), "0")));
                    brokDTO.setRemark2(StringUtil.nvl(map.get("REMARK")));
                    brokDTO.setBrok_reserve_flag2(StringUtil.nvl(map.get("BROK_RESERV_FLAG"), ""));
                    brokDTO.setBank_acc_id2(StringUtil.nvl(map.get("BANK_ACC_ID"), ""));
                    brokDTO.setBank_acc_desc2(StringUtil.nvl(map.get("BANK_ACC_DESC"), ""));
                }
                row = row + 1;
            }
        }
        if (row == 1) {
            sb = new StringBuilder();

            Map<String, Object> paramMap1 = new HashMap<>();
            paramMap1.put("vslCode", vslCode);
            paramMap1.put("voyNo", voyNo);
            paramMap1.put("chtInOutCode", chtInOutCode);

            List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saOwnerACSearch1", paramMap1);
            if (listMap1 != null && !listMap1.isEmpty()) {
                for (Map<String, Object> map : listMap1) {
                    if (!brok11.equals(map.get("BROK_ACC_CODE")) && !"".equals(map.get("BROK_ACC_CODE"))) {
                        if ((!"T".equals(chtInOutCode) && "KR".equals(StringUtil.nvl(map.get("BROK_NAT"), ""))) || ("KR".equals(StringUtil.nvl(map.get("ACC_NAT_CODE"), "")) && "KR".equals(StringUtil.nvl(map.get("BROK_NAT"), "")))) {
                            // 대선 국내 broker는 prepayment안되게 함, 용선에서 국내 선주, 국내 Broker의 경우 reserved 인되게 함
                        } else {
                            brokDTO.setBroker2(StringUtil.nvl(map.get("BROK_ACC_CODE")));
                            brokDTO.setBroker_name2(StringUtil.nvl(map.get("BROK_ACC_NAME"), ""));
                            brokDTO.setBrokerage_krw2(0.0);
                            brokDTO.setBrokerage_usd2(0.0);
                            brokDTO.setComm2(Formatter.nullDouble(StringUtil.nvl(map.get("BROK_COMM_RATE"), "0")));
                            brokDTO.setRemark2("");
                            brokDTO.setBrok_reserve_flag2("N");
                        }
                    } else if (!brok12.equals(map.get("BROK_ACC_CODE2")) && !"".equals(map.get("BROK_ACC_CODE2"))) {
                        if ((!"T".equals(chtInOutCode) && "KR".equals(StringUtil.nvl(map.get("BROK_NAT2"), ""))) || ("KR".equals(StringUtil.nvl(map.get("ACC_NAT_CODE"), "")) && "KR".equals(StringUtil.nvl(map.get("BROK_NAT2"), "")))) {
                            // 대선 국내 broker는 prepayment안되게 함, 용선에서 국내 선주, 국내 Broker의 경우 reserved 인되게 함
                        } else {
                            brokDTO.setBroker2(StringUtil.nvl(map.get("BROK_ACC_CODE2")));
                            brokDTO.setBroker_name2(StringUtil.nvl(map.get("BROK_ACC_NAME2"), ""));
                            brokDTO.setBrokerage_krw2(0.0);
                            brokDTO.setBrokerage_usd2(0.0);
                            brokDTO.setComm2(Formatter.nullDouble(StringUtil.nvl(map.get("BROK_COMM_RATE2"), "0")));
                            brokDTO.setRemark2("");
                            brokDTO.setBrok_reserve_flag2("N");
                        }
                    }
                }//while
            }//while
            brokDTO.setHire(0.0);
            brokDTO.setHire2(0.0);
        }
        if ("".equals(brChk)) {

            Map<String, Object> paramMap2 = new HashMap<>();
            paramMap2.put("vslCode", vslCode);
            paramMap2.put("voyNo", voyNo);
            paramMap2.put("chtInOutCode", chtInOutCode);

            List<Map<String, Object>> listMap2 = uxbDAO.select("OTCSADetail.saOwnerACSearch2", paramMap2);
            if (listMap2 != null && !listMap2.isEmpty()) {
                for (Map<String, Object> map : listMap2) {
                    if ((!"T".equals(chtInOutCode) && "KR".equals(Formatter.nullTrim(String.valueOf(map.get("BROK_NAT")))))
                            || ("KR".equals(Formatter.nullTrim(String.valueOf(map.get("ACC_NAT_CODE")))))
                            && "KR".equals(Formatter.nullTrim(String.valueOf(map.get("BROK_NAT"))))) {
                        // 대선 국내 broker는 prepayment안되게 함, 용선에서 국내 선주, 국내 Broker의 경우 reserved 인되게 함
                    } else {
                        brok_check = true;
                        brokDTO.setBroker(StringUtil.nvl(map.get("BROK_ACC_CODE")));
                        brokDTO.setBroker_name(Formatter.nullTrim(String.valueOf(map.get("BROK_ACC_NAME"))));
                        brokDTO.setBrokerage_krw(0.0);
                        brokDTO.setBrokerage_usd(0.0);
                        brokDTO.setComm(Formatter.nullDouble(StringUtil.nvl(map.get("BROK_COMM_RATE"), "0")));
                        brokDTO.setRemark("");
                        brokDTO.setBrok_reserve_flag("N");
                    }
                    if ((!"T".equals(chtInOutCode) && "KR".equals(Formatter.nullTrim(String.valueOf(map.get("BROK_NAT2")))))
                            || ("KR".equals(Formatter.nullTrim(String.valueOf(map.get("ACC_NAT_CODE")))))
                            && "KR".equals(Formatter.nullTrim(String.valueOf(map.get("BROK_NAT2"))))) {
                        // 대선 국내 broker는 prepayment안되게 함, 용선에서 국내 선주, 국내 Broker의 경우 reserved 인되게 함
                    } else {
                        if (brok_check) {
                            brokDTO.setBroker2(StringUtil.nvl(map.get("BROK_ACC_CODE2")));
                            brokDTO.setBroker_name2(Formatter.nullTrim(String.valueOf(map.get("BROK_ACC_NAME2"))));
                            brokDTO.setBrokerage_krw2(0.0);
                            brokDTO.setBrokerage_usd2(0.0);
                            brokDTO.setComm2(Formatter.nullDouble(StringUtil.nvl(map.get("BROK_COMM_RATE2"), "0")));
                            brokDTO.setRemark2("");
                            brokDTO.setBrok_reserve_flag2("N");
                        } else {
                            brokDTO.setBroker(StringUtil.nvl(map.get("BROK_ACC_CODE2")));
                            brokDTO.setBroker_name(Formatter.nullTrim(String.valueOf(map.get("BROK_ACC_NAME2"))));
                            brokDTO.setBrokerage_krw(0.0);
                            brokDTO.setBrokerage_usd(0.0);
                            brokDTO.setComm(Formatter.nullDouble(StringUtil.nvl(map.get("BROK_COMM_RATE2"), "0")));
                            brokDTO.setRemark("");
                            brokDTO.setBrok_reserve_flag("N");
                        }
                    }
                }
            }
            brokDTO.setHire(0.0);
            brokDTO.setHire2(0.0);
        }
		/*StringBuilder sb12 = new StringBuilder();

		Map<String, Object> paramMap3 = new HashMap<>();
		paramMap3.put("vslCode", vslCode);
		paramMap3.put("voyNo", voyNo);
		paramMap3.put("chtInOutCode", chtInOutCode);
		paramMap3.put("saNo", saNo);

		List<Map<String, Object>> listMap3 = uxbDAO.select("OTCSADetail.saOwnerACSearch3", paramMap3);
		double hireinit = 0;
		if(listMap3 != null && !listMap3.isEmpty()) {
			for(Map<String, Object> map : listMap3) {
				hireinit = rs12.getDouble("ONHIRE");
			}
		}
		if (hireinit <= 0) {
			brokDTO.setHire(0.0);
			brokDTO.setHire2(0.0);
		} else {*/
        //sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE = 'A001'    ");
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        //sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE = 'G001'    ");
        //sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE = 'H001'    ");
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        //sb11.append(" 						  WHERE SA_NO = ? AND TRSACT_CODE IN ('I003','I004')   ");
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        //OUT OF HIRE의 경우 BROKER FLAG가 Y일때만 합계에 포함한다.		111201 GYJ
        //sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE = 'A004'    ");
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218

        Map<String, Object> paramMap4 = new HashMap<>();
        paramMap4.put("saNo", saNo);

        List<Map<String, Object>> listMap4 = uxbDAO.select("OTCSADetail.saOwnerACSearch4", paramMap4);
        Double hire = null;
        if (listMap4 != null && !listMap4.isEmpty()) {
            for (Map<String, Object> map : listMap4) {
                hire = Formatter.nullDouble(StringUtil.nvl(map.get("HIRE"), "0"));
            }
        }
        brokDTO.setHire(hire);
        brokDTO.setHire2(hire);
        ////
        result.add(brokDTO);
        // **************************** Brokerage 가져오기 종료
        // **************************** //
        // **************************** Speed Claim 가져오기 시작
        // **************************** //
        String detailSql = "";
        detailSql = " SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ";
        detailSql = detailSql + "  FROM OTC_SA_DETAIL V    ";
        //sb1.append(" AND  V.trsact_code IN ('I002','I003','I004','I005','I006','I007','N001','N002') ORDER BY V.GROUP_SEQ ,V.TRSACT_CODE ");
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218

        Map<String, Object> paramMap5 = new HashMap<>();
        paramMap5.put("saNo", saNo);

        List<Map<String, Object>> listMap5 = uxbDAO.select("OTCSADetail.saOwnerACSearch5", paramMap5);
        OTCSaSpeedClaimDTO speedDTO = null;
        Collection speeds = new ArrayList<>();
        String trsactCd = "";
        row = 0;
        long group_seq = 0;
        long pre_group_seq = 0;
        if (listMap5 != null && !listMap5.isEmpty()) {
            for (Map<String, Object> map : listMap5) {
                trsactCd = String.valueOf(map.get("TRSACT_CODE"));
                group_seq = StringUtil.toLong((String) map.get("GROUP_SEQ"), 0L);
                if (row == 0) {
                    speedDTO = new OTCSaSpeedClaimDTO();
                    pre_group_seq = group_seq;
                } else if (group_seq != pre_group_seq) {
                    speeds.add(speedDTO);
                    speedDTO = new OTCSaSpeedClaimDTO();
                }
                //if ("I002".equals(trsactCd) || "I003".equals(trsactCd) || "I004".equals(trsactCd))
                //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
                if ("I002".equals(trsactCd) || "I003".equals(trsactCd) || "I071".equals(trsactCd) || "I004".equals(trsactCd) || "I072".equals(trsactCd)) {
                    speedDTO.setSpeed_claim_flag(StringUtil.nvl(map.get("OWN_SPD_CLM_FLAG")));
                    speedDTO.setSa_no(Formatter.nullDouble(StringUtil.nvl(map.get("SA_NO"), "0")));
                    speedDTO.setFrom_date(Formatter.parseToDate(map.get("FROM_DATE")));
                    speedDTO.setTo_date(Formatter.parseToDate(map.get("TO_DATE")));
                    speedDTO.setDuration(Formatter.nullDouble(StringUtil.nvl(map.get("SA_RATE_DUR"), "0")));
                    speedDTO.setDay_hire(Formatter.nullDouble(StringUtil.nvl(map.get("SA_RATE"), "0")));
                    speedDTO.setFactor(Formatter.nullDouble(StringUtil.nvl(map.get("FACTOR"), "0")));
                    speedDTO.setSpeed_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    speedDTO.setSpeed_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    speedDTO.setSpeed_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    speedDTO.setAmount_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                    speedDTO.setAmount_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                    speedDTO.setSpeed_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    speedDTO.setVat_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0")));
                    speedDTO.setVat_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0")));
                    speedDTO.setRemark(StringUtil.nvl(map.get("REMARK")));
                    // 채산 항차 추가 hjkanf 20090602
                    speedDTO.setVoyage(Formatter.nullLong(StringUtil.nvl(map.get("VOY_NO"), "0")));
                    speedDTO.setOrg_factor(Double.valueOf(100));
                    // else if ("I005".equals(trsactCd))
                    //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
                } else if ("I005".equals(trsactCd) || "I073".equals(trsactCd)) {
                    speedDTO.setSpeed_claim_flag(StringUtil.nvl(map.get("OWN_SPD_CLM_FLAG")));
                    speedDTO.setAdd_comm(Formatter.nullDouble(StringUtil.nvl(map.get("SA_RATE"), "0")));
                    speedDTO.setAdd_comm_amount_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                    speedDTO.setAdd_comm_amount_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                    speedDTO.setRsv_factor(Double.valueOf(100 - Formatter.nullDouble(StringUtil.nvl(speedDTO.getAdd_comm(), "0"))));
                    // 채산 항차 추가 ryu 20100203  add.comm 단독으로 생성하는경우 및 I003과 같이 생성하는 경우 모두 감안
                    speedDTO.setVoyage(Formatter.nullLong(StringUtil.nvl(map.get("VOY_NO"), "0")));
                } else if ("I006".equals(trsactCd) || "N001".equals(trsactCd)) {
                    speedDTO.setSpeed_claim_flag(StringUtil.nvl(map.get("OWN_SPD_CLM_FLAG")));
                    speedDTO.setFo_qty(Formatter.nullDouble(StringUtil.nvl(map.get("BNK_QTY"), "0")));
                    speedDTO.setFo_price(Formatter.nullDouble(StringUtil.nvl(map.get("BNK_PRC"), "0")));
                    speedDTO.setFo_amount_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                    speedDTO.setFo_amount_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                    speedDTO.setFo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    speedDTO.setFo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    speedDTO.setFo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    speedDTO.setFo_vat_amount_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0")));
                    speedDTO.setFo_vat_amount_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0")));
                    speedDTO.setFo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("I007".equals(trsactCd) || "N002".equals(trsactCd)) {
                    speedDTO.setSpeed_claim_flag(StringUtil.nvl(map.get("OWN_SPD_CLM_FLAG")));
                    speedDTO.setDo_qty(Formatter.nullDouble(StringUtil.nvl(map.get("BNK_QTY"), "0")));
                    speedDTO.setDo_price(Formatter.nullDouble(StringUtil.nvl(map.get("BNK_PRC"), "0")));
                    speedDTO.setDo_amount_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                    speedDTO.setDo_amount_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                    speedDTO.setDo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    speedDTO.setDo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    speedDTO.setDo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    speedDTO.setDo_vat_amount_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0")));
                    speedDTO.setDo_vat_amount_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0")));
                    speedDTO.setDo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                }
                row = row + 1;
                pre_group_seq = group_seq;
            }
        }
        if (row > 0) {
            speeds.add(speedDTO);
        }
        result.add(speeds);
        // **************************** Speed Claim 가져오기 종료
        // **************************** //
        // **************************** Owner's A/C 가져오기 시작
        // **************************** //

        Map<String, Object> paramMap6 = new HashMap<>();
        paramMap6.put("saNo", saNo);

        List<Map<String, Object>> listMap6 = uxbDAO.select("OTCSADetail.saOwnerACSearch6", paramMap6);
        OTCSaOwnerACDTO acDTO = null;
        Collection acs = new ArrayList<>();
        String trsact = "";
        if (listMap6 != null && !listMap6.isEmpty()) {
            for (Map<String, Object> map : listMap6) {
                acDTO = new OTCSaOwnerACDTO();
                trsact = Formatter.nullTrim(String.valueOf(map.get("TRSACT_CODE")));
                if ("I008".equals(trsact)) {
                    if ("T".equals(chtInOutCode)) {
                        acDTO.setItem_name("RESERVED/ESTIMATE");
                    } else {
                        acDTO.setItem_name("PREPAYMENT");
                    }
                } else if ("I009".equals(trsact)) {
                    acDTO.setItem_name("ON/OFF-HIRE SVY");
                } else if ("I010".equals(trsact)) {
                    acDTO.setItem_name("PORT CHARGE");
                } else if ("I011".equals(trsact)) {
                    acDTO.setItem_name("RESERVED CTM");
                } else if ("I012".equals(trsact)) {
                    acDTO.setItem_name("CARGO CHARGE");
                } else if ("I013".equals(trsact)) {
                    acDTO.setItem_name("A.COM");
                } else if ("I014".equals(trsact)) {
                    acDTO.setItem_name("BUNKER CHARGE");
                } else if ("I015".equals(trsact)) {
                    acDTO.setItem_name("DISPUTE(RESERVED)");
                } else if ("M005".equals(trsact)) {
                    acDTO.setItem_name("PREPAID INCOME TAX");
                } else if ("M006".equals(trsact)) {
                    acDTO.setItem_name("BUSINESS TAX");
                } else if ("I074".equals(trsact)) {        // 신규 거래유형(I074, I075) 추가 20150422 hijang
                    //acDTO.setItem_name("BUNKER CHARGE(FO)");
                    acDTO.setItem_name("FO SUPPLY BY CHARTERER");    //명칭변경 20180206 GYJ
                } else if ("I075".equals(trsact)) {        // 신규 거래유형(I074, I075) 추가 20150422 hijang
                    //acDTO.setItem_name("BUNKER CHARGE(DO)");
                    acDTO.setItem_name("DO SUPPLY BY CHARTERER");    //명칭변경 20180206 GYJ
                }
                acDTO.setItem(trsact);
                acDTO.setAmount_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                acDTO.setAmount_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                acDTO.setRemark(StringUtil.nvl(map.get("REMARK")));
                acDTO.setVat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                acDTO.setTax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                acDTO.setOrg_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                acDTO.setVat_amount_krw(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0")));
                acDTO.setVat_amount_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0")));
                acDTO.setTax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                acDTO.setBnk_prc(Formatter.nullDouble(StringUtil.nvl(map.get("BNK_PRC"), "0")));    // 신규 거래유형(I074, I075) 추가 20150422 hijang	 (BNK_PRC,  BNK_QTY 추가 )
                acDTO.setBnk_qty(Formatter.nullDouble(StringUtil.nvl(map.get("BNK_QTY"), "0")));    // 신규 거래유형(I074, I075) 추가 20150422 hijang	 (BNK_PRC,  BNK_QTY 추가 )
                acDTO.setBnk_type(StringUtil.nvl(map.get("BNK_TYPE")));    // BUNKER TYPE 추가 (hijang 20200924)
                // 채산 항차 추가 hjkanf 20090602
                acDTO.setVoyage(Formatter.nullLong(StringUtil.nvl(map.get("VOY_NO"), "0")));
                acDTO.setFactor(Formatter.nullDouble(StringUtil.nvl(map.get("FACTOR"), "0")));
                acs.add(acDTO);
            }
        }
        result.add(acs);
        // **************************** Owner's A/C 가져오기 종료
        // **************************** //
        return result;
    }

    public Collection saOwnerSettleRsvSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo, String stlFlag, String processFlag) {
        Collection result = new ArrayList<>();
        Collection rev = new ArrayList<>();
        Collection act = new ArrayList<>();
        Collection brk = new ArrayList<>();
        // String stlFlagCk = "";
        // String sExist = "";
        String invoice = "";

        // **************************** Reserved(Owners' Exp) 가져오기 시작
        // **************************** //

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", voyNo);
        paramMap.put("chtInOutCode", chtInOutCode);
        paramMap.put("saNo", saNo);
        paramMap.put("stlFlag", stlFlag);
        paramMap.put("processFlag", processFlag);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saOwnerSettleRsvSearch", paramMap);
        OTCSaOwnSettleDTO settleDTO = null;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                settleDTO = new OTCSaOwnSettleDTO();
                settleDTO.setCheck_item("1");
                settleDTO.setOp_team_code(StringUtil.nvl(map.get("OP_TEAM_CODE"), ""));
                settleDTO.setCntr_team_code(StringUtil.nvl(map.get("CNTR_TEAM_CODE"), ""));
                settleDTO.setStl_acc_code(StringUtil.nvl(map.get("STL_CNTR_ACC_CODE"), ""));
                settleDTO.setStl_acc_name(StringUtil.nvl(map.get("STL_ACC_NAME"), ""));
                settleDTO.setCurrency_code(StringUtil.nvl(map.get("CURRENCY_CODE"), ""));
                settleDTO.setEntered_amt(Formatter.nullDouble(StringUtil.nvl(map.get("ENTERED_AMT"), "0")));
                settleDTO.setUsd_amt(Formatter.nullDouble(StringUtil.nvl(map.get("USD_AMT"), "0")));
                settleDTO.setWon_amt(Formatter.nullDouble(StringUtil.nvl(map.get("WON_AMT"), "0")));
                settleDTO.setSlip_no(StringUtil.nvl(map.get("SLIP_NO"), ""));
                settleDTO.setGl_acct(StringUtil.nvl(map.get("GL_ACCT"), ""));
                settleDTO.setSa_no(Formatter.nullDouble(StringUtil.nvl(map.get("SA_NO"), "0")));
                settleDTO.setStl_flag(StringUtil.nvl(map.get("STL_FLAG"), ""));
                // stlFlagCk = settleDTO.getStl_flag();
                settleDTO.setStl_vsl_code(StringUtil.nvl(map.get("STL_VSL_CODE"), ""));
                settleDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("STL_VOY_NO"), "0")));
                settleDTO.setUsd_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                settleDTO.setLoc_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("LOC_SA_AMT"), "0")));
                settleDTO.setKrw_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                settleDTO.setStl_port_code(StringUtil.nvl(map.get("STL_PORT_CODE"), ""));
                settleDTO.setStl_erp_slip_no(StringUtil.nvl(map.get("STL_ERP_SLIP_NO"), ""));
                settleDTO.setRemark(StringUtil.nvl(map.get("REMARK"), ""));
                settleDTO.setCurcy_code(StringUtil.nvl(map.get("CURCY_CODE"), ""));
                settleDTO.setExc_date(Formatter.parseToDate(map.get("EXC_DATE")));
                //settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs.getTimestamp("exc_date")));   //RYU
                settleDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXC_DATE")));
                settleDTO.setExc_rate_type(StringUtil.nvl(map.get("EXC_RATE_TYPE"), ""));
                settleDTO.setUsd_exc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("USD_EXC_RATE"), "0")));
                settleDTO.setLoc_exc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("LOC_EXC_RATE"), "0")));
                settleDTO.setUsd_loc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("USD_LOC_RATE"), "0")));
                settleDTO.setExchange_rate_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_LOC_RATE"), "0")));
                settleDTO.setExchange_rate_krw(Formatter.nullDouble(StringUtil.nvl(map.get("LOC_EXC_RATE"), "0")));
                settleDTO.setDue_date(Formatter.parseToDate(map.get("DUE_DATE")));
                settleDTO.setPymt_term(StringUtil.nvl(map.get("PYMT_TERM"), ""));
                settleDTO.setTerms_date(Formatter.parseToDate(map.get("TERMS_DATE")));
                settleDTO.setPymt_hold_flag(StringUtil.nvl(map.get("PYMT_HOLD_FLAG"), ""));
                rev.add(settleDTO);
            }
        }
        result.add(rev);
        // **************************** Reserved(Owners' Exp) 가져오기 종료
        // **************************** //
        // **************************** Actual Owners' A/C(Tc/In) 가져오기 시작
        // **************************** //
        StringBuilder sb2 = new StringBuilder();

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", saNo);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saOwnerSettleRsvSearch1", paramMap1);
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap1) {
                settleDTO = new OTCSaOwnSettleDTO();
                settleDTO.setCheck_item("1");
                settleDTO.setOp_team_code(Formatter.nullTrim(String.valueOf(map.get("OP_TEAM_CODE"))));
                settleDTO.setCntr_team_code(Formatter.nullTrim(String.valueOf(map.get("CNTR_TEAM_CODE"))));
                settleDTO.setStl_acc_code(Formatter.nullTrim(String.valueOf(map.get("STL_CNTR_ACC_CODE"))));
                settleDTO.setStl_acc_name(Formatter.nullTrim(String.valueOf(map.get("STL_ACC_NAME"))));
                settleDTO.setCurrency_code(Formatter.nullTrim(String.valueOf(map.get("CURRENCY_CODE"))));
                settleDTO.setEntered_amt(Formatter.nullDouble(StringUtil.nvl(map.get("ENTERED_AMT"), "0")));
                settleDTO.setUsd_amt(Formatter.nullDouble(StringUtil.nvl(map.get("USD_AMT"), "0")));
                settleDTO.setWon_amt(Formatter.nullDouble(StringUtil.nvl(map.get("WON_AMT"), "0")));
                settleDTO.setSlip_no(Formatter.nullTrim(String.valueOf(map.get("SLIP_NO"))));
                settleDTO.setGl_acct(Formatter.nullTrim(String.valueOf(map.get("GL_ACCT"))));
                settleDTO.setSa_no(Formatter.nullDouble(StringUtil.nvl(map.get("SA_NO"), "0")));
                settleDTO.setStl_flag(Formatter.nullTrim(String.valueOf(map.get("STL_FLAG"))));
                // stlFlagCk = settleDTO.getStl_flag();
                settleDTO.setStl_vsl_code(Formatter.nullTrim(String.valueOf(map.get("STL_VSL_CODE"))));
                settleDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("STL_VOY_NO"), "0")));
                settleDTO.setUsd_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                settleDTO.setLoc_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("LOC_SA_AMT"), "0")));
                settleDTO.setKrw_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                settleDTO.setStl_port_code(Formatter.nullTrim(String.valueOf(map.get("STL_PORT_CODE"))));
                settleDTO.setStl_erp_slip_no(Formatter.nullTrim(String.valueOf(map.get("STL_ERP_SLIP_NO"))));
                settleDTO.setRemark(Formatter.nullTrim(String.valueOf(map.get("REMARK"))));
                settleDTO.setCurcy_code(Formatter.nullTrim(String.valueOf(map.get("CURCY_CODE"))));
                settleDTO.setExc_date(Formatter.parseToDate(map.get("EXC_DATE")));
                //settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(String.valueOf(map.get("exc_date"))));
                settleDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXC_DATE")));    //RYU 2010.07.14
                settleDTO.setExc_rate_type(Formatter.nullTrim(String.valueOf(map.get("EXC_RATE_TYPE"))));
                settleDTO.setUsd_exc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("USD_EXC_RATE"), "0")));
                settleDTO.setLoc_exc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("LOC_EXC_RATE"), "0")));
                settleDTO.setUsd_loc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("USD_LOC_RATE"), "0")));
                settleDTO.setExchange_rate_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_LOC_RATE"), "0")));
                settleDTO.setExchange_rate_krw(Formatter.nullDouble(StringUtil.nvl(map.get("LOC_EXC_RATE"), "0")));
                settleDTO.setDue_date(Formatter.parseToDate(map.get("DUE_DATE")));
                settleDTO.setPymt_term(Formatter.nullTrim(String.valueOf(map.get("PYMT_TERM"))));
                settleDTO.setTerms_date(Formatter.parseToDate(map.get("TERMS_DATE")));
                settleDTO.setPymt_hold_flag(Formatter.nullTrim(String.valueOf(map.get("PYMT_HOLD_FLAG"))));
                act.add(settleDTO);
            }
        }
        result.add(act);
        // **************************** Actual Owners' A/C(Tc/In) 가져오기 종료
        // **************************** //
        // **************************** Reserved(Brokerage) 가져오기 시작
        // **************************** //
        StringBuilder sb1 = new StringBuilder();

        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("vslCode", vslCode);
        paramMap2.put("voyNo", voyNo);
        paramMap2.put("chtInOutCode", chtInOutCode);
        paramMap2.put("saNo", saNo);
        paramMap2.put("stlFlag", stlFlag);
        paramMap2.put("processFlag", processFlag);

        List<Map<String, Object>> listMap2 = uxbDAO.select("OTCSADetail.saOwnerSettleRsvSearch2", paramMap2);
        if (listMap2 != null && !listMap2.isEmpty()) {
            for (Map<String, Object> map : listMap2) {
                settleDTO = new OTCSaOwnSettleDTO();
                settleDTO.setCheck_item("1");
                settleDTO.setOp_team_code(StringUtil.nvl(map.get("OP_TEAM_CODE"), ""));
                settleDTO.setCntr_team_code(StringUtil.nvl(map.get("CNTR_TEAM_CODE"), ""));
                settleDTO.setStl_acc_code(StringUtil.nvl(map.get("STL_CNTR_ACC_CODE"), ""));
                settleDTO.setStl_acc_name(StringUtil.nvl(map.get("STL_ACC_NAME"), ""));
                settleDTO.setCurrency_code(StringUtil.nvl(map.get("CURRENCY_CODE"), ""));
                settleDTO.setEntered_amt(Formatter.nullDouble(StringUtil.nvl(map.get("ENTERED_AMT"), "0")));
                settleDTO.setUsd_amt(Formatter.nullDouble(StringUtil.nvl(map.get("USD_AMT"), "0")));
                settleDTO.setWon_amt(Formatter.nullDouble(StringUtil.nvl(map.get("WON_AMT"), "0")));
                settleDTO.setSlip_no(StringUtil.nvl(map.get("SLIP_NO"), ""));
                settleDTO.setGl_acct(StringUtil.nvl(map.get("GL_ACCT"), ""));
                settleDTO.setSa_no(Formatter.nullDouble(StringUtil.nvl(map.get("SA_NO"), "0")));
                settleDTO.setStl_flag(StringUtil.nvl(map.get("STL_FLAG"), ""));
                // stlFlagCk = settleDTO.getStl_flag();
                settleDTO.setStl_vsl_code(StringUtil.nvl(map.get("STL_VSL_CODE"), ""));
                settleDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("STL_VOY_NO"), "0")));
                settleDTO.setUsd_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                settleDTO.setLoc_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("LOC_SA_AMT"), "0")));
                settleDTO.setKrw_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                settleDTO.setStl_port_code(StringUtil.nvl(map.get("STL_PORT_CODE"), ""));
                settleDTO.setStl_erp_slip_no(StringUtil.nvl(map.get("STL_ERP_SLIP_NO"), ""));
                settleDTO.setRemark(StringUtil.nvl(map.get("REMARK"), ""));
                settleDTO.setCurcy_code(StringUtil.nvl(map.get("CURCY_CODE"), ""));
                settleDTO.setExc_date(Formatter.parseToDate(map.get("EXC_DATE")));
                //settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(String.valueOf(map.get("exc_date"))));
                settleDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXC_DATE")));    //RYU 2010.07.14
                settleDTO.setExc_rate_type(StringUtil.nvl(map.get("EXC_RATE_TYPE"), ""));
                settleDTO.setUsd_exc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("USD_EXC_RATE"), "0")));
                settleDTO.setLoc_exc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("LOC_EXC_RATE"), "0")));
                settleDTO.setUsd_loc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("USD_LOC_RATE"), "0")));
                settleDTO.setExchange_rate_usd(Formatter.nullDouble(StringUtil.nvl(map.get("USD_LOC_RATE"), "0")));
                settleDTO.setExchange_rate_krw(Formatter.nullDouble(StringUtil.nvl(map.get("LOC_EXC_RATE"), "0")));
                settleDTO.setDue_date(Formatter.parseToDate(map.get("DUE_DATE")));
                settleDTO.setPymt_term(StringUtil.nvl(map.get("PYMT_TERM"), ""));
                settleDTO.setTerms_date(Formatter.parseToDate(map.get("TERMS_DATE")));
                settleDTO.setPymt_hold_flag(StringUtil.nvl(map.get("PYMT_HOLD_FLAG"), ""));
                brk.add(settleDTO);
            }
        }
        result.add(brk);
        // **************************** Reserved(Brokerage) 가져오기 종료
        // **************************** //
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Owner Settle역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     */
    public Collection saOwnerSettleApSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo, String stlFlag, String processFlag) {
        Collection result = new ArrayList<>();
        Collection rev = new ArrayList<>();
        Collection act = new ArrayList<>();
        // String stlFlagCk = "";
        // **************************** Account payable / Advance Received
        // 가져오기 시작 **************************** //

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saOwnerSettleApSearch", paramMap);
        OTCSaOwnSettleDTO settleDTO = null;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                settleDTO = new OTCSaOwnSettleDTO();
                settleDTO.setCheck_item("1");
                settleDTO.setOp_team_code(StringUtil.nvl(map.get("OP_TEAM_CODE"), ""));
                settleDTO.setCntr_team_code(StringUtil.nvl(map.get("CNTR_TEAM_CODE"), ""));
                settleDTO.setStl_acc_code(StringUtil.nvl(map.get("STL_CNTR_ACC_CODE"), ""));
                settleDTO.setStl_acc_name(StringUtil.nvl(map.get("STL_ACC_NAME"), ""));
                settleDTO.setCurrency_code(StringUtil.nvl(map.get("CURRENCY_CODE"), ""));
                settleDTO.setEntered_amt(Formatter.nullDouble(StringUtil.nvl(map.get("ENTERED_AMT"), "0")));
                settleDTO.setUsd_amt(Formatter.nullDouble(StringUtil.nvl(map.get("USD_AMT"), "0")));
                settleDTO.setWon_amt(Formatter.nullDouble(StringUtil.nvl(map.get("WON_AMT"), "0")));
                settleDTO.setSlip_no(StringUtil.nvl(map.get("SLIP_NO"), ""));
                settleDTO.setGl_acct(StringUtil.nvl(map.get("GL_ACCT"), ""));
                settleDTO.setSa_no(Formatter.nullDouble(StringUtil.nvl(map.get("SA_NO"), "0")));
                settleDTO.setStl_flag(StringUtil.nvl(map.get("STL_FLAG"), ""));
                // stlFlagCk = settleDTO.getStl_flag();
                settleDTO.setStl_vsl_code(StringUtil.nvl(map.get("STL_VSL_CODE"), ""));
                settleDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("STL_VOY_NO"), "0")));
                settleDTO.setUsd_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("USD_SA_AMT"), "0")));
                settleDTO.setLoc_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("LOC_SA_AMT"), "0")));
                settleDTO.setKrw_sa_amt(Formatter.nullDouble(StringUtil.nvl(map.get("KRW_SA_AMT"), "0")));
                settleDTO.setStl_port_code(StringUtil.nvl(map.get("STL_PORT_CODE"), ""));
                settleDTO.setStl_erp_slip_no(StringUtil.nvl(map.get("STL_ERP_SLIP_NO"), ""));
                settleDTO.setRemark(StringUtil.nvl(map.get("REMARK"), ""));
                settleDTO.setCurcy_code(StringUtil.nvl(map.get("CURCY_CODE"), ""));
                settleDTO.setExc_date(Formatter.parseToDate(map.get("EXC_DATE")));
                //settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs.getTimestamp("exc_date")));
                settleDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXC_DATE")));    //RYU 2010.07.14
                settleDTO.setExc_rate_type(StringUtil.nvl(map.get("EXC_RATE_TYPE"), ""));
                settleDTO.setUsd_exc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("USD_EXC_RATE"), "0")));
                settleDTO.setLoc_exc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("LOC_EXC_RATE"), "0")));
                settleDTO.setUsd_loc_rate(Formatter.nullDouble(StringUtil.nvl(map.get("USD_LOC_RATE"), "0")));
                settleDTO.setExchange_rate_usd(Double.valueOf(StringUtil.nvl(map.get("USD_LOC_RATE"), "0.0")));
                settleDTO.setExchange_rate_krw(Double.valueOf(StringUtil.nvl(map.get("LOC_EXC_RATE"), "0.0")));
                settleDTO.setDue_date(Formatter.parseToDate(map.get("DUE_DATE")));
                settleDTO.setPymt_term(StringUtil.nvl(map.get("PYMT_TERM"), ""));
                settleDTO.setTerms_date(Formatter.parseToDate(map.get("TERMS_DATE")));
                settleDTO.setPymt_hold_flag(StringUtil.nvl(map.get("PYMT_HOLD_FLAG"), ""));
                rev.add(settleDTO);
            }
        }
        result.add(rev);
        // **************************** Account payable / Advance Received
        // 가져오기 종료 **************************** //
        // **************************** Account Receivable 가져오기 시작
        // **************************** //
        StringBuilder sb2 = new StringBuilder();

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", saNo);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saOwnerSettleApSearch1", paramMap1);
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap1) {
                settleDTO = new OTCSaOwnSettleDTO();
                settleDTO.setCheck_item("1");
                settleDTO.setOp_team_code(Formatter.nullTrim(String.valueOf(map.get("OP_TEAM_CODE"))));
                settleDTO.setCntr_team_code(Formatter.nullTrim(String.valueOf(map.get("CNTR_TEAM_CODE"))));
                settleDTO.setStl_acc_code(Formatter.nullTrim(String.valueOf(map.get("STL_CNTR_ACC_CODE"))));
                settleDTO.setStl_acc_name(Formatter.nullTrim(String.valueOf(map.get("STL_ACC_NAME"))));
                settleDTO.setCurrency_code(Formatter.nullTrim(String.valueOf(map.get("CURRENCY_CODE"))));
                settleDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("ENTERED_AMT"), "0.0")));
                settleDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("USD_AMT"), "0.0")));
                settleDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("WON_AMT"), "0.0")));
                settleDTO.setSlip_no(Formatter.nullTrim(String.valueOf(map.get("SLIP_NO"))));
                settleDTO.setGl_acct(Formatter.nullTrim(String.valueOf(map.get("GL_ACCT"))));
                settleDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                settleDTO.setStl_flag(Formatter.nullTrim(String.valueOf(map.get("STL_FLAG"))));
                // stlFlagCk = settleDTO.getStl_flag();
                settleDTO.setStl_vsl_code(Formatter.nullTrim(String.valueOf(map.get("STL_VSL_CODE"))));
                settleDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("STL_VOY_NO"), "0")));
                settleDTO.setUsd_sa_amt(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                settleDTO.setLoc_sa_amt(Double.valueOf(StringUtil.nvl(map.get("LOC_SA_AMT"), "0.0")));
                settleDTO.setKrw_sa_amt(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                settleDTO.setStl_port_code(Formatter.nullTrim(String.valueOf(map.get("STL_PORT_CODE"))));
                settleDTO.setStl_erp_slip_no(Formatter.nullTrim(String.valueOf(map.get("STL_ERP_SLIP_NO"))));
                settleDTO.setRemark(Formatter.nullTrim(String.valueOf(map.get("REMARK"))));
                settleDTO.setCurcy_code(Formatter.nullTrim(String.valueOf(map.get("CURCY_CODE"))));
                settleDTO.setExc_date(Formatter.parseToDate(map.get("EXC_DATE")));
                //settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(String.valueOf(map.get("exc_date"))));
                settleDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXC_DATE")));   //RYU 2010.07.14
                settleDTO.setExc_rate_type(Formatter.nullTrim(String.valueOf(map.get("EXC_RATE_TYPE"))));
                settleDTO.setUsd_exc_rate(Double.valueOf(StringUtil.nvl(map.get("USD_EXC_RATE"), "0.0")));
                settleDTO.setLoc_exc_rate(Double.valueOf(StringUtil.nvl(map.get("LOC_EXC_RATE"), "0.0")));
                settleDTO.setUsd_loc_rate(Double.valueOf(StringUtil.nvl(map.get("USD_LOC_RATE"), "0.0")));
                settleDTO.setExchange_rate_usd(Double.valueOf(StringUtil.nvl(map.get("USD_LOC_RATE"), "0.0")));
                settleDTO.setExchange_rate_krw(Double.valueOf(StringUtil.nvl(map.get("LOC_EXC_RATE"), "0.0")));
                settleDTO.setDue_date(Formatter.parseToDate(map.get("DUE_DATE")));
                settleDTO.setPymt_term(Formatter.nullTrim(String.valueOf(map.get("PYMT_TERM"))));
                settleDTO.setTerms_date(Formatter.parseToDate(map.get("TERMS_DATE")));
                settleDTO.setPymt_hold_flag(Formatter.nullTrim(String.valueOf(map.get("PYMT_HOLD_FLAG"))));
                act.add(settleDTO);
            }
        }
        result.add(act);
        // **************************** Account Receivable 가져오기 종료
        // **************************** //
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public Collection saOnHireSelect(Long saNo, Long orgSaNo, String amdCode) {
        Collection result = new ArrayList<>();
        // **************************** OnHire 가져오기 시작
        // **************************** //
        StringBuilder sb = new StringBuilder();
        //sb.append(" AND  V.trsact_code IN ('A001' , 'A002') ORDER BY V.GROUP_SEQ , V.TRSACT_CODE ");
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saOnHireSelect", paramMap);
        OTCSaOnHireDTO onHireDTO = null;
        Collection hires = new ArrayList<>();
        int row = 0;
        long group_seq = 0;
        long pre_group_seq = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                // TRSACT_CODE인 A001와 A002는 한쌍으로 존재 할때만 collection(hires)에 담는다.
                group_seq = Formatter.nullLong(StringUtil.nvl(map.get("GROUP_SEQ"), "0"));
                if (row == 0) {
                    onHireDTO = new OTCSaOnHireDTO();
                    pre_group_seq = group_seq;
                } else if (group_seq != pre_group_seq) {
                    hires.add(onHireDTO);
                    onHireDTO = new OTCSaOnHireDTO();
                }
                //if ("A001".equals(rs.getString("TRSACT_CODE")))
                //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
                if ("A001".equals(String.valueOf(map.get("TRSACT_CODE"))) || "A006".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    onHireDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    onHireDTO.setFrom_date(Formatter.parseToDate(map.get("FROM_DATE")));
                    onHireDTO.setTo_date(Formatter.parseToDate(map.get("TO_DATE")));
                    onHireDTO.setDur(Double.valueOf(StringUtil.nvl(map.get("SA_RATE_DUR"), "0.0")));
                    onHireDTO.setDay_hire(Double.valueOf(StringUtil.nvl(map.get("SA_RATE"), "0.0")));
                    onHireDTO.setAmount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    onHireDTO.setAmount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    // bbc 처리 관련 vat 정보 setting hjkang 20090813
                    onHireDTO.setVat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    onHireDTO.setTax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    onHireDTO.setOrg_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    onHireDTO.setTax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    onHireDTO.setVat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    onHireDTO.setVat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                }
                //if ("A002".equals(rs.getString("TRSACT_CODE")))
                //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
                if ("A002".equals(String.valueOf(map.get("TRSACT_CODE"))) || "A007".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    onHireDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    onHireDTO.setAdd_comm(Double.valueOf(StringUtil.nvl(map.get("SA_RATE"), "0.0")));
                    onHireDTO.setAdd_comm_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    onHireDTO.setAdd_comm_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                }
                row = row + 1;
                pre_group_seq = group_seq;
            }
        }
        if (row > 0) {
            hires.add(onHireDTO);
        }
        result.add(hires);
        // **************************** OnHire 가져오기 종료
        // **************************** //
        // **************************** CVE 가져오기 시작
        // **************************** //
        sb.setLength(0);

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", saNo);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saOnHireSelect1", paramMap1);
        OTCSaCVEDTO cveDTO = null;
        Collection cves = new ArrayList<>();
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap1) {
                cveDTO = new OTCSaCVEDTO();
                cveDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                cveDTO.setVat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                cveDTO.setTax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                cveDTO.setOrg_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                cveDTO.setTax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                cveDTO.setAmount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                cveDTO.setAmount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                cveDTO.setVat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                cveDTO.setVat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                cveDTO.setRemark(StringUtil.nvl(map.get("REMARK")));
                //170323 GYJ
                cveDTO.setCve_flag(StringUtil.nvl(map.get("CVE_FLAG")));
                cveDTO.setFrom_date(Formatter.parseToDate(map.get("FROM_DATE")));
                cveDTO.setTo_date(Formatter.parseToDate(map.get("TO_DATE")));
                cves.add(cveDTO);
            }
        }
        result.add(cves);
        // **************************** CVE 가져오기 종료
        // **************************** //
        long org_sa_no = 0;
        String amd_code = "";
        log.debug("orgSaNo = " + orgSaNo);
        // 수정세금계산서로 신규 발행하는 건으로 Open하지 않았으므로 기존에 수정세금계산서로 등록돼 있는지 확인한다.
        if (orgSaNo == null || orgSaNo.longValue() == 0) {
            log.debug("orgSaNo null or o 이면 amd_trx_no로 원본 sa 읽어오기 ");
            sb.setLength(0);
            // 수정세금계산서라면 원본 SA_NO  읽어오기
            log.debug("원본 sa_no, amd 가져오기" + sb.toString());

            Map<String, Object> paramMap2 = new HashMap<>();
            paramMap2.put("saNo", saNo);

            List<Map<String, Object>> listMap2 = uxbDAO.select("OTCSADetail.saOnHireSelect2", paramMap2);
            if (listMap2 != null && !listMap2.isEmpty()) {
                for (Map<String, Object> map : listMap2) {
                    org_sa_no = Long.valueOf(StringUtil.nvl(map.get("ORG_TRX_NO"), "0"));
                    amd_code = String.valueOf(map.get("AMD_CODE"));
                }
            }
        } else { // 수정세금계산서로 신규 발행하는 건으로 Open 하였으므로 연계 번호를 그대로 읽어온다.
            log.debug("orgSaNo not null or not o 이면 param으로 가져오기 ");
            org_sa_no = Formatter.nullZero(orgSaNo).longValue();
            amd_code = amdCode;
        }
        Collection orgSaInfo = new ArrayList<>();
        Collection orgTaxInfo = new ArrayList<>();
        if (org_sa_no == 0) { // 수정세금계산서가 존재하지 않으면 아래 로직을 불필요
            result.add(orgSaInfo);  // 원본 master 정보
            result.add(orgTaxInfo);  // 원본 tax list 정보
        } else {  // // 수정 세금계산서가 존재 할시 start
            sb.setLength(0);
            log.debug("원본 sa_no, exc rate, gl_date 가져오기" + sb.toString());

            Map<String, Object> paramMap3 = new HashMap<>();
            paramMap3.put("org_sa_no", org_sa_no);

            List<Map<String, Object>> listMap3 = uxbDAO.select("OTCSADetail.saOnHireSelect3", paramMap3);
            OTCSaHeadDTO orgSaDTO = null;
            if (listMap3 != null && !listMap3.isEmpty()) {
                for (Map<String, Object> map : listMap3) {
                    orgSaDTO = new OTCSaHeadDTO();
                    orgSaDTO.setSa_no(Formatter.nullLong(StringUtil.nvl(map.get("SA_NO"), "0")));
                    orgSaDTO.setPosting_date(Formatter.parseToDate(map.get("POSTING_DATE")));
                    orgSaDTO.setLoc_exc_rate(Double.valueOf(StringUtil.nvl(map.get("LOC_EXC_RATE"), "0.0")));
                    orgSaDTO.setAmend_code(amd_code);   // 수정 코드
                    orgSaInfo.add(orgSaDTO);
                }
            }
            result.add(orgSaInfo);
            //	**************************** 수정세금계산서 원본 List 가져오기 시작
            // **************************** //
            sb.setLength(0);
            log.debug("원본 tax list  가져오기" + sb.toString());

            Map<String, Object> paramMap4 = new HashMap<>();
            paramMap4.put("org_sa_no", org_sa_no);

            List<Map<String, Object>> listMap4 = uxbDAO.select("OTCSADetail.saOnHireSelect4", paramMap4);
            CCDTaxDetailDTO taxDTO = null;
            if (listMap4 != null && !listMap4.isEmpty()) {
                for (Map<String, Object> map : listMap4) {
                    taxDTO = new CCDTaxDetailDTO();
                    taxDTO.setVat_no(Formatter.nullLong(StringUtil.nvl(map.get("VAT_NO"), "0")));
                    taxDTO.setVat_seq(Formatter.nullLong(StringUtil.nvl(map.get("S_SEQ"), "0")));
                    taxDTO.setSa_seq(Formatter.nullLong(StringUtil.nvl(map.get("SEQ"), "0")));
                    taxDTO.setTrsact_name(StringUtil.nvl(map.get("S_TITLE")));
                    taxDTO.setItem_desc(StringUtil.nvl(map.get("ITEM_DESC")));
                    taxDTO.setTrsact_code(StringUtil.nvl(map.get("TRSACT_CODE")));
                    taxDTO.setTax_code_id(StringUtil.nvl(map.get("TAX_CODE_ID")));
                    taxDTO.setKrw_base_amt(Formatter.nullLong(StringUtil.nvl(map.get("KRW_BASE_AMT"), "0")));
                    taxDTO.setKrw_vat_amt(Formatter.nullLong(StringUtil.nvl(map.get("KRW_VAT_AMT"), "0")));
                    orgTaxInfo.add(taxDTO);
                }
            }
            result.add(orgTaxInfo);
            //			**************************** 수정세금계산서 원본 List 가져오기 종료
            // **************************** //
        }  // 수정 세금계산서가 존재 할시 end
        //	**************************** 저장된 선수금 gl_date 가져오기 시작 (2011.03.08 GYJ)
        // **************************** //
        sb.setLength(0);
        log.debug("저장된 선수금 gl_date 가져오기" + sb.toString());

        Map<String, Object> paramMap5 = new HashMap<>();
        paramMap5.put("saNo", saNo);

        List<Map<String, Object>> listMap5 = uxbDAO.select("OTCSADetail.saOnHireSelect5", paramMap5);
        EARIfReceiptBalanceVDTO RecieptDTO = null;
        Collection reciepts = new ArrayList<>();
        if (listMap5 != null && !listMap5.isEmpty()) {
            for (Map<String, Object> map : listMap5) {
                RecieptDTO = new EARIfReceiptBalanceVDTO();
                RecieptDTO.setGl_Date(Formatter.parseToDate(map.get("adv_gl_date")));
                reciepts.add(RecieptDTO);
            }
        }
        result.add(reciepts);
        //	**************************** 저장된 선수금 gl_date 가져오기 종료
        // **************************** //
        //	**************************** 저장된 미결  max(gl_date) 가져오기 시작 (2011.03.23 GYJ)
        // **************************** //
        sb.setLength(0);
        //sb.append("   /*+ opt_param('_optimizer_cost_based_transformation','off') */   \n")		;	//150109 GYJ 11g 속도개선 힌트추
        //sb.append("   /* opt_param('_optimizer_cost_based_transformation','off') */   \n")		;	//220104 GYJ 속도개선 힌트추가
        //sb.append("	  from (select TO_DATE(SUBSTR(a.stl_erp_slip_no,2,8),'YYYY-MM-DD') as GL_DATE									\n");
        //sb.append("			  from otc_sa_detail a	\n");
        //sb.append("             AND SUBSTR(a.Stl_Erp_Slip_No, 1,1)='I' -- AP  Invoice 만. (receipt, TRX 제외 )       \n");
        log.debug("저장된 선수금 gl_date 가져오기" + sb.toString());

        Map<String, Object> paramMap6 = new HashMap<>();
        paramMap6.put("saNo", saNo);

        List<Map<String, Object>> listMap6 = uxbDAO.select("OTCSADetail.saOnHireSelect6", paramMap6);
        OTCSaHeadDTO settleGlDateDTO = null;
        Collection settle = new ArrayList<>();
        if (listMap6 != null && !listMap6.isEmpty()) {
            for (Map<String, Object> map : listMap6) {
            	if(map == null) continue;
                settleGlDateDTO = new OTCSaHeadDTO();
                settleGlDateDTO.setPosting_date(Formatter.parseToDate(map.get("gl_date")));
                settle.add(settleGlDateDTO);
            }
        }
        result.add(settle);
        //	**************************** 저장된 미결  max(gl_date) 가져오기 종료
        // **************************** //
        //			 **************************** Out of Hire 가져오기 시작 111115 GYJ
        // **************************** //
        sb.setLength(0);
        //sb.append(" AND  V.trsact_code IN ('A004', 'A005') ORDER BY V.GROUP_SEQ , V.TRSACT_CODE ");
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        log.debug(sb.toString());

        Map<String, Object> paramMap7 = new HashMap<>();
        paramMap7.put("saNo", saNo);

        List<Map<String, Object>> listMap7 = uxbDAO.select("OTCSADetail.saOnHireSelect7", paramMap7);
        //OTCSaOutHireDTO outHireDTO = null;
        OTCSaOnHireDTO outHireDTO = null;
        Collection outhires = new ArrayList<>();
        int row1 = 0;
        long group_seq1 = 0;
        long pre_group_seq1 = 0;
        if (listMap7 != null && !listMap7.isEmpty()) {
            for (Map<String, Object> map : listMap7) {
                // TRSACT_CODE인 A004와 A005는 한쌍으로 존재 할때만 collection(hires)에 담는다.
                group_seq1 = StringUtil.toLong((String) map.get("GROUP_SEQ"), 0L);
                if (row1 == 0) {
                    //outHireDTO = new OTCSaOutHireDTO();
                    outHireDTO = new OTCSaOnHireDTO();
                    pre_group_seq1 = group_seq1;
                } else if (group_seq1 != pre_group_seq1) {
                    outhires.add(outHireDTO);
                    //outHireDTO = new OTCSaOutHireDTO();
                    outHireDTO = new OTCSaOnHireDTO();
                }
                //if ("A004".equals(rs.getString("TRSACT_CODE")))
                //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
                if ("A004".equals(String.valueOf(map.get("TRSACT_CODE"))) || "A008".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    outHireDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    outHireDTO.setFrom_date(Formatter.parseToDate(map.get("FROM_DATE")));
                    outHireDTO.setTo_date(Formatter.parseToDate(map.get("TO_DATE")));
                    outHireDTO.setDur(Double.valueOf(StringUtil.nvl(map.get("SA_RATE_DUR"), "0.0")));
                    outHireDTO.setDay_hire(Double.valueOf(StringUtil.nvl(map.get("SA_RATE"), "0.0")));
                    outHireDTO.setAmount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    outHireDTO.setAmount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    outHireDTO.setBrok_reserve_flag(StringUtil.nvl(map.get("BROK_RESERV_FLAG")));        //111128 GYJ
                    outHireDTO.setRemark(StringUtil.nvl(map.get("REMARK")));        //111128 GYJ
                    // bbc 처리 관련 vat 정보 setting hjkang 20090813
                    outHireDTO.setVat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    outHireDTO.setTax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    outHireDTO.setOrg_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    outHireDTO.setTax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    outHireDTO.setVat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    outHireDTO.setVat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                }
                //if ("A005".equals(rs.getString("TRSACT_CODE")))
                //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
                if ("A005".equals(String.valueOf(map.get("TRSACT_CODE"))) || "A009".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    outHireDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    outHireDTO.setAdd_comm(Double.valueOf(StringUtil.nvl(map.get("SA_RATE"), "0.0")));
                    outHireDTO.setAdd_comm_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    outHireDTO.setAdd_comm_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                }
                row1 = row1 + 1;
                pre_group_seq1 = group_seq1;
            }
        }
        if (row1 > 0) {
            outhires.add(outHireDTO);
        }
        result.add(outhires);
        // **************************** Out of Hire  가져오기 종료
        // **************************** //
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public Collection saBbcLngDetailInquiryt(Long saNo) {
        Collection result = new ArrayList<>();
        // **************************** OnHire 가져오기 시작
        // **************************** //

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBbcLngDetailInquiryt", paramMap);
        OTCSaDetailDTO detailDTO = null;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                detailDTO = new OTCSaDetailDTO();
                detailDTO.setSa_no(Formatter.nullLong(StringUtil.nvl(map.get("SA_NO"), "0")));
                detailDTO.setSa_seq(Formatter.nullLong(StringUtil.nvl(map.get("SA_SEQ"), "0")));
                detailDTO.setTrsact_code(StringUtil.nvl(map.get("TRSACT_CODE")));
                detailDTO.setFrom_date(Formatter.parseToDate(map.get("FROM_DATE")));
                detailDTO.setTo_date(Formatter.parseToDate(map.get("TO_DATE")));
                detailDTO.setSa_rate(Double.valueOf(StringUtil.nvl(map.get("SA_RATE"), "0.0")));
                detailDTO.setSa_rate_dur(Double.valueOf(StringUtil.nvl(map.get("SA_RATE_DUR"), "0.0")));
                detailDTO.setLoc_sa_amt(Double.valueOf(StringUtil.nvl(map.get("LOC_SA_AMT"), "0.0")));
                detailDTO.setKrw_sa_amt(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                detailDTO.setVat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                detailDTO.setTax_code_flag(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                detailDTO.setLoc_vat_sa_amt(Double.valueOf(StringUtil.nvl(map.get("LOC_VAT_SA_AMT"), "0.0")));
                detailDTO.setKrw_vat_sa_amt(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                detailDTO.setRemark(StringUtil.nvl(map.get("REMARK")));
                detailDTO.setCourt_flag(StringUtil.nvl(map.get("COURT_FLAG")));                     // 법원허가번호 추가 (hijang 20140313 )
                detailDTO.setCourt_admit_no(StringUtil.nvl(map.get("COURT_ADMIT_NO"))); // 법원허가번호 추가 (hijang 20140313 )
                result.add(detailDTO);
            }
        }
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     */
    public Collection saOffHireSelect(Long saNo) {
        Collection result = null;
        // **************************** NegoAmount/Compensation 가져오기 시작
        // **************************** //
        //sb.append(" AND  V.trsact_code  = 'H005' ORDER BY V.SA_SEQ ");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);
        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saOffHireSelect", paramMap);
        OTCSaOffHireNegoDTO negoDTO = new OTCSaOffHireNegoDTO();
        result = new ArrayList<>();
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                negoDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                negoDTO.setAmount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                negoDTO.setAmount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                negoDTO.setVat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                negoDTO.setTax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                negoDTO.setOrg_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                negoDTO.setTax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                negoDTO.setVat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                negoDTO.setVat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                //채산항차 추가 170523 GYJ
                negoDTO.setVoyage(Formatter.nullLong(StringUtil.nvl(map.get("VOY_NO"), "0")));
                //remark 추가 221025 GYJ
                negoDTO.setRemark(StringUtil.nvl(map.get("REMARK")));
            }
        }
        result.add(negoDTO);
        // **************************** NegoAmount/Compensation 가져오기 종료
        // **************************** //
        // **************************** Off Hire 가져오기 시작
        // **************************** //
        StringBuilder sb1 = new StringBuilder();
        //sb1.append(" AND  V.trsact_code IN ('H001','H002','H003','H004','H006','H007','H008')  ORDER BY V.GROUP_SEQ,V.TRSACT_CODE ");
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", saNo);
        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saOffHireSelect1", paramMap1);
        OTCSaOffHireDTO offDTO = null;
        Collection offs = new ArrayList<>();
        int row = 0;
        long group_seq = 0;
        long pre_group_seq = 0;
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap1) {
                group_seq = Formatter.nullLong(StringUtil.nvl(map.get("GROUP_SEQ"), ""));
                if (row == 0) {
                    offDTO = new OTCSaOffHireDTO();
                    pre_group_seq = group_seq;
                } else if (group_seq != pre_group_seq) {
                    offs.add(offDTO);
                    offDTO = new OTCSaOffHireDTO();
                }
                //if ("H001".equals(Formatter.nullTrim(String.valueOf(map.get("TRSACT_CODE")))) || "H006".equals(Formatter.nullTrim(String.valueOf(map.get("TRSACT_CODE")))))
                //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
                if ("H001".equals(StringUtil.nvl(map.get("TRSACT_CODE"), ""))
                        || "H009".equals(StringUtil.nvl(map.get("TRSACT_CODE"), ""))
                        || "H006".equals(StringUtil.nvl(map.get("TRSACT_CODE"), ""))) {
                    offDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    offDTO.setFrom_date(Formatter.parseToDate(map.get("FROM_DATE")));
                    offDTO.setTo_date(Formatter.parseToDate(map.get("TO_DATE")));
                    offDTO.setDuration(Double.valueOf(StringUtil.nvl(map.get("SA_RATE_DUR"), "0.0")));
                    offDTO.setDay_hire(Double.valueOf(StringUtil.nvl(map.get("SA_RATE"), "0.0")));
                    offDTO.setFactor(Double.valueOf(StringUtil.nvl(map.get("FACTOR"), "0.0")));
                    offDTO.setAmount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    offDTO.setAmount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    offDTO.setRemark(StringUtil.nvl(map.get("REMARK")));
                    offDTO.setSupplement(StringUtil.nvl(map.get("SUPPLEMENT")));
                    offDTO.setStl_flag(StringUtil.nvl(map.get("STL_FLAG"), ""));
                    offDTO.setOwn_spd_clm_flag(StringUtil.nvl(map.get("OWN_SPD_CLM_FLAG"), ""));
                    offDTO.setVat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    offDTO.setTax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    offDTO.setOrg_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    offDTO.setTax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    offDTO.setVat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    offDTO.setVat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    // else if ("H002".equals(Formatter.nullTrim(String.valueOf(map.get("TRSACT_CODE")))))
                    //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
                } else if ("H002".equals(StringUtil.nvl(map.get("TRSACT_CODE"), "")) || "H010".equals(StringUtil.nvl(map.get("TRSACT_CODE"), ""))) {
                    offDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    offDTO.setAdd_comm(Double.valueOf(StringUtil.nvl(map.get("SA_RATE"), "0.0")));
                    offDTO.setAdd_comm_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    offDTO.setAdd_comm_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    offDTO.setStl_flag(StringUtil.nvl(map.get("STL_FLAG"), ""));
                    offDTO.setOwn_spd_clm_flag(StringUtil.nvl(map.get("OWN_SPD_CLM_FLAG"), ""));
                } else if ("H003".equals(StringUtil.nvl(map.get("TRSACT_CODE"), "")) || "H007".equals(StringUtil.nvl(map.get("TRSACT_CODE"), ""))) {
                    offDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    offDTO.setFo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    offDTO.setFo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    offDTO.setFo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    offDTO.setFo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    offDTO.setFo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    offDTO.setFo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    offDTO.setFo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    offDTO.setFo_vat_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    offDTO.setFo_vat_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    offDTO.setFo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    offDTO.setStl_flag(StringUtil.nvl(map.get("STL_FLAG"), ""));
                    offDTO.setOwn_spd_clm_flag(StringUtil.nvl(map.get("OWN_SPD_CLM_FLAG"), ""));
                } else if ("H004".equals(StringUtil.nvl(map.get("TRSACT_CODE"), "")) || "H008".equals(StringUtil.nvl(map.get("TRSACT_CODE"), ""))) {
                    offDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    offDTO.setDo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    offDTO.setDo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    offDTO.setDo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    offDTO.setDo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    offDTO.setDo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    offDTO.setDo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    offDTO.setDo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    offDTO.setDo_vat_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    offDTO.setDo_vat_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    offDTO.setDo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    offDTO.setStl_flag(StringUtil.nvl(map.get("STL_FLAG"), ""));
                    offDTO.setOwn_spd_clm_flag(StringUtil.nvl(map.get("OWN_SPD_CLM_FLAG"), ""));
                }
                row = row + 1;
                pre_group_seq = group_seq;
            } // while
        } // while
        if (row > 0) {
            offs.add(offDTO);
        }
        result.add(offs);
        // **************************** Off Hire 가져오기 종료
        // **************************** //
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     *
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public Collection saHcleanSearch(Long saNo) {
        Collection result = null;
        // **************************** Hold Cleaning 가져오기 시작
        // **************************** //
        StringBuilder sb = new StringBuilder();
        String dSql = " SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*  FROM OTC_SA_DETAIL V   ";
        //sb.append(" AND  V.trsact_code =  'F001' ORDER BY V.SA_SEQ ");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saHcleanSearch", paramMap);
        result = new ArrayList<>();
        OTCSaHCleanDTO hcleanDTO = null;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                hcleanDTO = new OTCSaHCleanDTO();
                hcleanDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                hcleanDTO.setAmount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                hcleanDTO.setAmount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                hcleanDTO.setRemark(StringUtil.nvl(map.get("REMARK")));
                hcleanDTO.setVoyage(Formatter.nullLong(StringUtil.nvl(map.get("VOY_NO"), "0")));
                hcleanDTO.setOrg_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
            }
        }
        result.add(hcleanDTO);
        // **************************** Hold Cleaning 가져오기 종료
        // **************************** //
        // **************************** Intermediate Hold Cleaning 가져오기 시작
        // **************************** //s
        //sb1.append(" AND  V.trsact_code ='F002'  ORDER BY V.SA_SEQ ");

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", saNo);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saHcleanSearch1", paramMap1);
        OTCSaInterHCleanDTO interDTO = null;
        Collection inters = new ArrayList<>();
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap1) {
                interDTO = new OTCSaInterHCleanDTO();
                interDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                interDTO.setAmount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                interDTO.setAmount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                interDTO.setVessel(StringUtil.nvl(map.get("VSL_CODE")));
                interDTO.setVoyage(Formatter.nullLong(StringUtil.nvl(map.get("VOY_NO"), "0")));
                interDTO.setOrg_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                interDTO.setRemark(StringUtil.nvl(map.get("REMARK")));
                inters.add(interDTO);
            }
        }
        result.add(inters);
        // **************************** Intermediate Hold Cleaning 가져오기 종료
        // **************************** //
        // **************************** Ballast Bonus 가져오기 시작
        // **************************** //
        StringBuilder sb2 = new StringBuilder();
        //sb2.append(" AND  V.trsact_code IN ('G001','G002')  ORDER BY V.SA_SEQ ");

        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("saNo", saNo);

        List<Map<String, Object>> listMap2 = uxbDAO.select("OTCSADetail.saHcleanSearch2", paramMap2);
        OTCSaBallstBonusDTO ballstDTO = new OTCSaBallstBonusDTO();
        if (listMap2 != null && !listMap2.isEmpty()) {
            for (Map<String, Object> map : listMap2) {
                ballstDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                //if ("G001".equals(Formatter.nullTrim(String.valueOf(map.get("TRSACT_CODE")))))
                //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150204
                if ("G001".equals(StringUtil.nvl(map.get("TRSACT_CODE"), "")) || "G003".equals(StringUtil.nvl(map.get("TRSACT_CODE"), ""))) {
                    ballstDTO.setAmount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    ballstDTO.setAmount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    ballstDTO.setRemark(StringUtil.nvl(map.get("REMARK")));
                    ballstDTO.setVat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    ballstDTO.setTax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    ballstDTO.setOrg_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    ballstDTO.setTax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    ballstDTO.setVat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    ballstDTO.setVat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    //채산항차 추가 170523 GYJ
                    //ballstDTO.setVoyage((String.valueOf(map.get("VOY_NO"))));
                    // else if ("G002".equals(Formatter.nullTrim(String.valueOf(map.get("TRSACT_CODE")))))
                    //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150204
                } else if ("G002".equals(StringUtil.nvl(map.get("TRSACT_CODE"), "")) || "G004".equals(StringUtil.nvl(map.get("TRSACT_CODE"), ""))) {
                    ballstDTO.setAdd_comm(Double.valueOf(StringUtil.nvl(map.get("SA_RATE"), "0.0")));
                    ballstDTO.setAdd_comm_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    ballstDTO.setAdd_comm_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                }
            }
        }
        result.add(ballstDTO);
        // **************************** Ballast Bonus 가져오기 종료
        // **************************** //
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public Collection saBunkerSelect(Long saNo) {
        Collection result = new ArrayList<>();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBunkerSelect", paramMap);
        OTCSaBunkerDTO bunkerBODDTO = null;
        Collection BODs = new ArrayList<>();
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                if ("B001".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBODDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBODDTO.setBod_fo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBODDTO.setBod_fo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBODDTO.setBod_fo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_fo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_fo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBODDTO.setBod_fo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBODDTO.setBod_fo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_fo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_fo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B002".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBODDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBODDTO.setBod_do_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBODDTO.setBod_do_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBODDTO.setBod_do_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_do_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_do_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBODDTO.setBod_do_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBODDTO.setBod_do_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_do_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_do_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B003".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBODDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBODDTO.setBod_lsfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBODDTO.setBod_lsfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBODDTO.setBod_lsfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_lsfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_lsfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBODDTO.setBod_lsfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBODDTO.setBod_lsfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_lsfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_lsfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B004".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBODDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBODDTO.setBod_lsdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBODDTO.setBod_lsdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBODDTO.setBod_lsdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_lsdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_lsdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBODDTO.setBod_lsdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBODDTO.setBod_lsdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_lsdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_lsdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    //MS FO/DO 추가 190709
                } else if ("B005".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBODDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBODDTO.setBod_msfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBODDTO.setBod_msfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBODDTO.setBod_msfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_msfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_msfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBODDTO.setBod_msfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBODDTO.setBod_msfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_msfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_msfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B006".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBODDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBODDTO.setBod_msdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBODDTO.setBod_msdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBODDTO.setBod_msdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_msdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_msdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBODDTO.setBod_msdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBODDTO.setBod_msdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_msdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBODDTO.setBod_msdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                }
                BODs.add(bunkerBODDTO);
            }
        }
        result.add(BODs);

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", saNo);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saBunkerSelect1", paramMap1);
        OTCSaBunkerDTO bunkerBORDTO = null;
        Collection BORs = new ArrayList<>();
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap1) {
                if ("C001".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBORDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBORDTO.setBor_fo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBORDTO.setBor_fo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBORDTO.setBor_fo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_fo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_fo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBORDTO.setBor_fo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBORDTO.setBor_fo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_fo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_fo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C002".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBORDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBORDTO.setBor_do_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBORDTO.setBor_do_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBORDTO.setBor_do_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_do_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_do_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBORDTO.setBor_do_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBORDTO.setBor_do_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_do_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_do_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C003".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBORDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBORDTO.setBor_lsfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBORDTO.setBor_lsfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBORDTO.setBor_lsfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_lsfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_lsfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBORDTO.setBor_lsfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBORDTO.setBor_lsfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_lsfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_lsfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C004".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBORDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBORDTO.setBor_lsdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBORDTO.setBor_lsdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBORDTO.setBor_lsdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_lsdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_lsdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBORDTO.setBor_lsdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBORDTO.setBor_lsdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_lsdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_lsdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                }
                //MS FO/DO 추가 190709
                else if ("C005".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBORDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBORDTO.setBor_msfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBORDTO.setBor_msfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBORDTO.setBor_msfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_msfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_msfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBORDTO.setBor_msfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBORDTO.setBor_msfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_msfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_msfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C006".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bunkerBORDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bunkerBORDTO.setBor_msdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bunkerBORDTO.setBor_msdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bunkerBORDTO.setBor_msdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_msdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_msdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bunkerBORDTO.setBor_msdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bunkerBORDTO.setBor_msdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_msdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bunkerBORDTO.setBor_msdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                }
                BORs.add(bunkerBORDTO);
            }
        }
        result.add(BORs);
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public OTCSaBunkerDTO saBunkerSelect_20080722(Long saNo) {
        OTCSaBunkerDTO result = null;
        // Query 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBunkerSelect_20080722", paramMap);
        int row = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                if (row == 0)
                    result = new OTCSaBunkerDTO();
                if ("B001".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_fo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_fo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_fo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_fo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_fo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_fo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_fo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_fo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_fo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B002".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_do_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_do_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_do_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_do_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_do_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_do_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_do_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_do_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_do_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B003".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_lsfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_lsfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_lsfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_lsfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_lsfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_lsfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_lsfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_lsfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_lsfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B004".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_lsdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_lsdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_lsdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_lsdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_lsdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_lsdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_lsdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_lsdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_lsdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    //MS FO/DO 추가 190709
                } else if ("B005".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_msfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_msfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_msfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_msfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_msfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_msfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_msfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_msfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_msfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B006".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_msdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_msdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_msdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_msdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_msdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_msdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_msdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_msdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_msdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C001".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_fo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_fo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_fo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_fo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_fo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_fo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_fo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_fo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_fo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C002".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_do_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_do_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_do_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_do_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_do_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_do_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_do_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_do_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_do_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C003".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_lsfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_lsfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_lsfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_lsfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_lsfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_lsfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_lsfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_lsfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_lsfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C004".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_lsdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_lsdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_lsdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_lsdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_lsdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_lsdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_lsdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_lsdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_lsdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    //MS FO/DO 추가 190709
                } else if ("C005".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_msfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_msfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_msfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_msfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_msfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_msfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_msfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_msfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_msfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C006".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_msdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_msdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_msdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_msdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_msdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_msdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_msdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_msdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_msdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                }
                row = row + 1;
            }
        }
        return result;
    }

    /**
     * <p> By KGW 20080721...BOD/BOR 분리
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public OTCSaBunkerDTO saBunkerBODSelect_20080723(Long saNo) {
        OTCSaBunkerDTO result = null;
        // Query 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBunkerBODSelect_20080723", paramMap);
        int row = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                if (row == 0)
                    result = new OTCSaBunkerDTO();
                if ("B001".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_fo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_fo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_fo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_fo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_fo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_fo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_fo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_fo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_fo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B002".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_do_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_do_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_do_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_do_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_do_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_do_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_do_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_do_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_do_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B003".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_lsfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_lsfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_lsfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_lsfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_lsfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_lsfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_lsfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_lsfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_lsfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B004".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_lsdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_lsdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_lsdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_lsdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_lsdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_lsdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_lsdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_lsdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_lsdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    //MS FO/DO 추가 190709
                } else if ("B005".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_msfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_msfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_msfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_msfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_msfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_msfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_msfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_msfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_msfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B006".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBod_msdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBod_msdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBod_msdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBod_msdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBod_msdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBod_msdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBod_msdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBod_msdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBod_msdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                }
                row = row + 1;
            }
        }
        return result;
    }

    public Collection saBunkerBODSelect(Long saNo) {
        Collection result = new ArrayList<>();
        OTCSaBunkerDTO bodDTO = null;
        // Query 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBunkerBODSelect", paramMap);
        int row = 0;
        long group_seq = 0;
        long pre_group_seq = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                group_seq = StringUtil.toLong((String) map.get("GROUP_SEQ"), 0L);
                if (row == 0) {
                    bodDTO = new OTCSaBunkerDTO();
                    pre_group_seq = group_seq;
                } else if (group_seq != pre_group_seq) {
                    result.add(bodDTO);
                    bodDTO = new OTCSaBunkerDTO();
                }
                if ("B001".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bodDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bodDTO.setBod_fo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bodDTO.setBod_fo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bodDTO.setBod_fo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bodDTO.setBod_fo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bodDTO.setBod_fo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bodDTO.setBod_fo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bodDTO.setBod_fo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    bodDTO.setBod_fo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_fo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_fo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B002".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bodDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bodDTO.setBod_do_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bodDTO.setBod_do_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bodDTO.setBod_do_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bodDTO.setBod_do_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bodDTO.setBod_do_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bodDTO.setBod_do_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bodDTO.setBod_do_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    bodDTO.setBod_do_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_do_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_do_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B003".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bodDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bodDTO.setBod_lsfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bodDTO.setBod_lsfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bodDTO.setBod_lsfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bodDTO.setBod_lsfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bodDTO.setBod_lsfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bodDTO.setBod_lsfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bodDTO.setBod_lsfo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    bodDTO.setBod_lsfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_lsfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_lsfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B004".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bodDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bodDTO.setBod_lsdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bodDTO.setBod_lsdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bodDTO.setBod_lsdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bodDTO.setBod_lsdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bodDTO.setBod_lsdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bodDTO.setBod_lsdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bodDTO.setBod_lsdo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    bodDTO.setBod_lsdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_lsdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_lsdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    //MS FO/DO 추가 190709
                } else if ("B005".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bodDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bodDTO.setBod_msfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bodDTO.setBod_msfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bodDTO.setBod_msfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bodDTO.setBod_msfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bodDTO.setBod_msfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bodDTO.setBod_msfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bodDTO.setBod_msfo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    bodDTO.setBod_msfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_msfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_msfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("B006".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    bodDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    bodDTO.setBod_msdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    bodDTO.setBod_msdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    bodDTO.setBod_msdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    bodDTO.setBod_msdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    bodDTO.setBod_msdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    bodDTO.setBod_msdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    bodDTO.setBod_msdo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    bodDTO.setBod_msdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_msdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    bodDTO.setBod_msdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                }
                row = row + 1;
                pre_group_seq = group_seq;
            }
        }
        if (row > 0) {
            result.add(bodDTO);
        }
        return result;
    }

    /**
     * <p> By KGW 20080721...BOD/BOR 분리
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public OTCSaBunkerDTO saBunkerBORSelect_20080723(Long saNo) {
        OTCSaBunkerDTO result = null;
        // Query 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBunkerBORSelect_20080723", paramMap);
        int row = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                if (row == 0)
                    result = new OTCSaBunkerDTO();
                if ("C001".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_fo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_fo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_fo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_fo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_fo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_fo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_fo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_fo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_fo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C002".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_do_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_do_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_do_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_do_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_do_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_do_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_do_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_do_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_do_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C003".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_lsfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_lsfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_lsfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_lsfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_lsfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_lsfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_lsfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_lsfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_lsfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C004".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_lsdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_lsdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_lsdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_lsdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_lsdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_lsdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_lsdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_lsdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_lsdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    //MS FO/DO 추가 190709
                } else if ("C005".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_msfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_msfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_msfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_msfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_msfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_msfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_msfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_msfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_msfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C006".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    result.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    result.setBor_msdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    result.setBor_msdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    result.setBor_msdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    result.setBor_msdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    result.setBor_msdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    result.setBor_msdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    result.setBor_msdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    result.setBor_msdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    result.setBor_msdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                }
                row = row + 1;
            }
        }
        return result;
    }

    public Collection saBunkerBORSelect(Long saNo) {
        Collection result = new ArrayList<>();
        OTCSaBunkerDTO borDTO = null;
        // Query 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBunkerBORSelect", paramMap);
        int row = 0;
        long group_seq = 0;
        long pre_group_seq = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                group_seq = Formatter.nullLong(StringUtil.nvl(map.get("GROUP_SEQ"), "0"));
                if (row == 0) {
                    borDTO = new OTCSaBunkerDTO();
                    pre_group_seq = group_seq;
                } else if (group_seq != pre_group_seq) {
                    result.add(borDTO);
                    borDTO = new OTCSaBunkerDTO();
                }
                if ("C001".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    borDTO.setBor_fo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_fo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    borDTO.setBor_fo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    borDTO.setBor_fo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    borDTO.setBor_fo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    borDTO.setBor_fo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    borDTO.setBor_fo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    borDTO.setBor_fo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_fo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_fo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C002".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    borDTO.setBor_do_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_do_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    borDTO.setBor_do_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    borDTO.setBor_do_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    borDTO.setBor_do_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    borDTO.setBor_do_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    borDTO.setBor_do_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    borDTO.setBor_do_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_do_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_do_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C003".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    borDTO.setBor_lsfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_lsfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    borDTO.setBor_lsfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    borDTO.setBor_lsfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    borDTO.setBor_lsfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    borDTO.setBor_lsfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    borDTO.setBor_lsfo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    borDTO.setBor_lsfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_lsfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_lsfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C004".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    borDTO.setBor_lsdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_lsdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    borDTO.setBor_lsdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    borDTO.setBor_lsdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    borDTO.setBor_lsdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    borDTO.setBor_lsdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    borDTO.setBor_lsdo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    borDTO.setBor_lsdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_lsdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_lsdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                    //MS FO/DO 추가 190709
                } else if ("C005".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    borDTO.setBor_msfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_msfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    borDTO.setBor_msfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    borDTO.setBor_msfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    borDTO.setBor_msfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    borDTO.setBor_msfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    borDTO.setBor_msfo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    borDTO.setBor_msfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_msfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_msfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("C006".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    borDTO.setBor_msdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_msdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    borDTO.setBor_msdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    borDTO.setBor_msdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    borDTO.setBor_msdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    borDTO.setBor_msdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    borDTO.setBor_msdo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    borDTO.setBor_msdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_msdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    borDTO.setBor_msdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                }
                row = row + 1;
                pre_group_seq = group_seq;
            }
        }
        if (row > 0) {
            result.add(borDTO);
        }
        return result;
    }

    public Collection saBunkerBehalfSelect(Long saNo) {
        Collection result = new ArrayList<>();
        OTCSaBunkerDTO behalfDTO = null;
        // Query 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBunkerBehalfSelect", paramMap);
        int row = 0;
        long group_seq = 0;
        long pre_group_seq = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                group_seq = StringUtil.toLong((String) map.get("GROUP_SEQ"), 0L);
                if (row == 0) {
                    behalfDTO = new OTCSaBunkerDTO();
                    pre_group_seq = group_seq;
                } else if (group_seq != pre_group_seq) {
                    result.add(behalfDTO);
                    behalfDTO = new OTCSaBunkerDTO();
                }
                if ("E001".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    behalfDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    behalfDTO.setBehalf_fo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    behalfDTO.setBehalf_fo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    behalfDTO.setBehalf_fo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_fo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_fo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    behalfDTO.setBehalf_fo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    behalfDTO.setBehalf_fo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    behalfDTO.setBehalf_fo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_fo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_fo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("E002".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    behalfDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    behalfDTO.setBehalf_do_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    behalfDTO.setBehalf_do_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    behalfDTO.setBehalf_do_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_do_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_do_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    behalfDTO.setBehalf_do_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    behalfDTO.setBehalf_do_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    behalfDTO.setBehalf_do_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_do_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_do_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("E003".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    behalfDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    behalfDTO.setBehalf_lsfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    behalfDTO.setBehalf_lsfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    behalfDTO.setBehalf_lsfo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_lsfo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_lsfo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    behalfDTO.setBehalf_lsfo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    behalfDTO.setBehalf_lsfo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    behalfDTO.setBehalf_lsfo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_lsfo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_lsfo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                } else if ("E004".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    behalfDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("SA_NO"), "0.0")));
                    behalfDTO.setBehalf_lsdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    behalfDTO.setBehalf_lsdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    behalfDTO.setBehalf_lsdo_amount_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_lsdo_amount_usd(Double.valueOf(StringUtil.nvl(map.get("USD_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_lsdo_vat_flag(StringUtil.nvl(map.get("VAT_FLAG")));
                    behalfDTO.setBehalf_lsdo_tax_code(StringUtil.nvl(map.get("TAX_CODE_FLAG")));
                    behalfDTO.setBehalf_lsdo_org_vat_no(Formatter.nullLong(StringUtil.nvl(map.get("ORG_VAT_NO"), "0")));
                    behalfDTO.setBehalf_lsdo_vat_krw(Double.valueOf(StringUtil.nvl(map.get("KRW_VAT_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_lsdo_vat_usd(Double.valueOf(StringUtil.nvl(map.get("USD_VAT_SA_AMT"), "0.0")));
                    behalfDTO.setBehalf_lsdo_tax_code_name(StringUtil.nvl(map.get("TAX_CODE_NAME")));
                }
                row = row + 1;
                pre_group_seq = group_seq;
            }
        }
        if (row > 0) {
            result.add(behalfDTO);
        }
        return result;
    }

    public Collection saBunkerFirstBODSelect(String vslCode, Long voyNo, String tcFlag) {
        Collection result = new ArrayList<>();
        OTCSaBunkerDTO borDTO = null;
        StringBuilder sb = new StringBuilder();
        // Query 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", voyNo);
        paramMap.put("tcFlag", tcFlag);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBunkerFirstBODSelect", paramMap);
        int row = 0;
        long group_seq = 0;
        long pre_group_seq = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                group_seq = Formatter.nullLong(StringUtil.nvl(map.get("GROUP_SEQ"), "0"));
                if (row == 0) {
                    borDTO = new OTCSaBunkerDTO();
                    pre_group_seq = group_seq;
                } else if (group_seq != pre_group_seq) {
                    result.add(borDTO);
                    borDTO = new OTCSaBunkerDTO();
                }
                if ("B001".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setBor_fo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_fo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                } else if ("B002".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setBor_do_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_do_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                } else if ("B003".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setBor_lsfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_lsfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                } else if ("B004".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setBor_lsdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_lsdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                    //MS FO/DO 추가 190709
                } else if ("B005".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setBor_msfo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_msfo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                } else if ("B006".equals(String.valueOf(map.get("TRSACT_CODE")))) {
                    borDTO.setBor_msdo_qty(Double.valueOf(StringUtil.nvl(map.get("BNK_QTY"), "0.0")));
                    borDTO.setBor_msdo_price(Double.valueOf(StringUtil.nvl(map.get("BNK_PRC"), "0.0")));
                }
                row = row + 1;
                pre_group_seq = group_seq;
            }
        }
        if (row > 0) {
            result.add(borDTO);
        }
        return result;
    }

    /**
     * <p>
     * 설명:saHead내역을 삭제하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA HEAD 테이블에 삭제할 시 발생하는 메소드를 리턴한다.
     * saHeadDelete 실행하다 발생하는 모든 Exception을 처리한다
     */
    public String saDetailDelete(Long saNo, Long saSeq) throws Exception {
        String result = "";
        StringBuilder sb = new StringBuilder();
        // Query 가져오기
        sb.append(" SELECT  V.*   ");
        sb.append("          FROM OTC_SA_DETAIL V       ");
        sb.append(" WHERE SA_NO = " + saNo + " ");
        sb.append(" AND SA_SEQ = " + saSeq + " ");
        commonDao.setObject(OTCSaDetailVO.class, sb.toString(), 4);
        result = "SUC-0600";
        return result;
    }

    /**
     * <p>
     * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 open item 을 읽어온다. openType : OE ,
     * RB, AP, AC, AR chtInOutCOde : O, T, R
     */
    public Collection saOpenItemSearch(String vslCode, Long voyNo, String chtInOutCode, String accCode, String teamCode, String openType, String openTab, String postingDate, String stl_flag) {
        Collection result = new ArrayList<>();
        stl_flag = Formatter.nullTrim(stl_flag);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", Formatter.nullTrim(vslCode));
        paramMap.put("voyNo", Formatter.nullLong(voyNo));
        paramMap.put("chtInOutCode", chtInOutCode);
        paramMap.put("accCode", Formatter.nullTrim(accCode));
        paramMap.put("teamCode", Formatter.nullTrim(teamCode));
        paramMap.put("openType", openType);
        paramMap.put("openTab", openTab);
        paramMap.put("postingDate", postingDate);
        paramMap.put("stl_flag", stl_flag);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saOpenItemSearch", paramMap);
        OTCSaOpenItemDTO openDTO = null;
        OTCSaOwnSettleDTO stlDTO = null;
        String glAcd = "";
        double usd_rate = 0;
        double krw_rate = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                if ("OP".equals(openTab)) {
                    openDTO = new OTCSaOpenItemDTO();
                    if ("OE".equals(openType) || "RB".equals(openType) || "AP".equals(openType)) {
                        glAcd = StringUtil.nvl(map.get("SEGMENT3"), "");
                        openDTO.setItem_gubun(saOpenType(glAcd));
                        openDTO.setVsl(StringUtil.nvl(map.get("SEGMENT4"), ""));
                        openDTO.setVoy(StringUtil.nvl(map.get("SEGMENT5"), ""));
                        openDTO.setCurrency_code(StringUtil.nvl(map.get("CURRENCY_CODE"), ""));
                        openDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("ENTERED_AMT"), "0.0")));
                        openDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("USD_AMT"), "0.0")));
                        openDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("WON_AMT"), "0.0")));
                        openDTO.setDept(StringUtil.nvl(map.get("SEGMENT2"), ""));
                        openDTO.setPort(StringUtil.nvl(map.get("PORT_CODE"), ""));
                        openDTO.setStl_port_code(StringUtil.nvl(map.get("PORT_CODE"), ""));
                        openDTO.setAcc_name(StringUtil.nvl(map.get("VENDOR_NAME"), ""));
                        openDTO.setAcc_code(StringUtil.nvl(map.get("CUSTOMER_UNIQUE_ID"), ""));
                        openDTO.setSlip_no(StringUtil.nvl(map.get("INVOICE_NUMBER"), ""));
                        openDTO.setGl_account(glAcd);
                        openDTO.setRemark(StringUtil.nvl(map.get("COMMENTS"), ""));
                        openDTO.setGl_date(Formatter.parseToDate(map.get("GL_DATE")));
                        krw_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_KRW"), "0.0"))), 4);
                        usd_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_USD"), "0.0"))), 4);
                        openDTO.setExchange_rate_krw(Double.valueOf(krw_rate));
                        openDTO.setExchange_rate_usd(Double.valueOf(usd_rate));
                        //openDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
                        openDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXCHANGE_RATE_DATE_KRW")));   //RYU
                    } else if ("AC".equals(openType) || "AR".equals(openType)) {
                        glAcd = StringUtil.nvl(map.get("SEGMENT3"), "");
                        openDTO.setItem_gubun(saOpenType(glAcd));
                        openDTO.setVsl(StringUtil.nvl(map.get("SEGMENT4"), ""));
                        openDTO.setVoy(StringUtil.nvl(map.get("SEGMENT5"), ""));
                        openDTO.setCurrency_code(StringUtil.nvl(map.get("CURRENCY_CODE"), ""));
                        openDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("ENTERED_AMT"), "0.0")));
                        openDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("USD_AMT"), "0.0")));
                        openDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("WON_AMT"), "0.0")));
                        openDTO.setDept(StringUtil.nvl(map.get("SEGMENT2"), ""));
                        openDTO.setPort(StringUtil.nvl(map.get("PORT_CODE"), ""));
                        openDTO.setStl_port_code(StringUtil.nvl(map.get("PORT_CODE"), ""));
                        openDTO.setAcc_name(StringUtil.nvl(map.get("CUSTOMER_NAME"), ""));
                        openDTO.setAcc_code(StringUtil.nvl(map.get("CUSTOMER_UNIQUE_ID"), ""));
                        openDTO.setSlip_no(StringUtil.nvl(map.get("TRX_NUMBER"), ""));
                        openDTO.setGl_account(glAcd);
                        openDTO.setRemark(StringUtil.nvl(map.get("COMMENTS"), ""));
                        openDTO.setGl_date(Formatter.parseToDate(map.get("GL_DATE")));
                        krw_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_KRW"), "0.0"))), 4);
                        usd_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_USD"), "0.0"))), 4);
                        openDTO.setExchange_rate_krw(Double.valueOf(krw_rate));
                        openDTO.setExchange_rate_usd(Double.valueOf(usd_rate));
                        //openDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
                        openDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXCHANGE_RATE_DATE_KRW")));   //RYU
                    } else if ("".equals(openType)) {
                        glAcd = StringUtil.nvl(map.get("SEGMENT3"), "");
                        openDTO.setItem_gubun(saOpenType(glAcd));
                        openDTO.setVsl(StringUtil.nvl(map.get("SEGMENT4"), ""));
                        openDTO.setVoy(StringUtil.nvl(map.get("SEGMENT5"), ""));
                        openDTO.setCurrency_code(StringUtil.nvl(map.get("CURRENCY_CODE"), ""));
                        openDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("ENTERED_AMT"), "0.0")));
                        openDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("USD_AMT"), "0.0")));
                        openDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("WON_AMT"), "0.0")));
                        openDTO.setDept(StringUtil.nvl(map.get("SEGMENT2"), ""));
                        openDTO.setPort(StringUtil.nvl(map.get("PORT_CODE"), ""));
                        openDTO.setStl_port_code(StringUtil.nvl(map.get("PORT_CODE"), ""));
                        openDTO.setAcc_name(StringUtil.nvl(map.get("CUSTOMER_NAME"), ""));
                        openDTO.setAcc_code(StringUtil.nvl(map.get("CUSTOMER_UNIQUE_ID"), ""));
                        openDTO.setSlip_no(StringUtil.nvl(map.get("INVOICE_NUMBER"), ""));
                        openDTO.setGl_account(glAcd);
                        openDTO.setRemark(StringUtil.nvl(map.get("COMMENTS"), ""));
                        openDTO.setGl_date(Formatter.parseToDate(map.get("GL_DATE")));
                        krw_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_KRW"), "0.0"))), 4);
                        usd_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_USD"), "0.0"))), 4);
                        openDTO.setExchange_rate_krw(Double.valueOf(krw_rate));
                        openDTO.setExchange_rate_usd(Double.valueOf(usd_rate));
                        //openDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
                        openDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXCHANGE_RATE_DATE_KRW")));  //RYU
                    }
                    result.add(openDTO);
                } else {
                    stlDTO = new OTCSaOwnSettleDTO();
                    if ("OE".equals(openType) || "RB".equals(openType) || "AP".equals(openType)) {
                        stlDTO.setCheck_item("0");
                        glAcd = StringUtil.nvl(map.get("SEGMENT3"), "");
                        stlDTO.setStl_vsl_code(StringUtil.nvl(map.get("SEGMENT4"), ""));
                        stlDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("SEGMENT5"), "0")));
                        stlDTO.setCurrency_code(StringUtil.nvl(map.get("CURRENCY_CODE"), ""));
                        stlDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("ENTERED_AMT"), "0.0")));
                        stlDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("USD_AMT"), "0.0")));
                        stlDTO.setUsd_sa_amt(Double.valueOf(StringUtil.nvl(map.get("USD_AMT"), "0.0")));
                        stlDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("WON_AMT"), "0.0")));
                        stlDTO.setOp_team_code(StringUtil.nvl(map.get("SEGMENT2"), ""));
                        stlDTO.setStl_port_code(StringUtil.nvl(map.get("PORT_CODE"), ""));
                        stlDTO.setStl_acc_name(StringUtil.nvl(map.get("VENDOR_NAME"), ""));
                        stlDTO.setStl_acc_code(StringUtil.nvl(map.get("CUSTOMER_UNIQUE_ID"), ""));
                        stlDTO.setSlip_no(StringUtil.nvl(map.get("INVOICE_NUMBER"), ""));
                        stlDTO.setGl_acct(glAcd);
                        stlDTO.setRemark(StringUtil.nvl(map.get("COMMENTS"), ""));
                        stlDTO.setGl_date(Formatter.parseToDate(map.get("GL_DATE")));
                        krw_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_KRW"), "0.0"))), 4);
                        usd_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_USD"), "0.0"))), 4);
                        stlDTO.setExchange_rate_krw(Double.valueOf(krw_rate));
                        stlDTO.setExchange_rate_usd(Double.valueOf(usd_rate));
                        //stlDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
                        stlDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXCHANGE_RATE_DATE_KRW")));
                    } else if ("AC".equals(openType) || "AR".equals(openType)) {
                        stlDTO.setCheck_item("0");
                        glAcd = StringUtil.nvl(map.get("SEGMENT3"), "");
                        stlDTO.setStl_vsl_code(StringUtil.nvl(map.get("SEGMENT4"), ""));
                        stlDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("SEGMENT5"), "0")));
                        stlDTO.setCurrency_code(StringUtil.nvl(map.get("CURRENCY_CODE"), ""));
                        stlDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("ENTERED_AMT"), "0.0")));
                        stlDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("USD_AMT"), "0.0")));
                        stlDTO.setUsd_sa_amt(Double.valueOf(StringUtil.nvl(map.get("USD_AMT"), "0.0")));
                        stlDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("WON_AMT"), "0.0")));
                        stlDTO.setOp_team_code(StringUtil.nvl(map.get("SEGMENT2"), ""));
                        stlDTO.setStl_port_code(StringUtil.nvl(map.get("PORT_CODE"), ""));
                        stlDTO.setStl_acc_name(StringUtil.nvl(map.get("CUSTOMER_NAME"), ""));
                        stlDTO.setStl_acc_code(StringUtil.nvl(map.get("CUSTOMER_UNIQUE_ID"), ""));
                        stlDTO.setSlip_no(StringUtil.nvl(map.get("TRX_NUMBER"), ""));
                        stlDTO.setGl_acct(glAcd);
                        stlDTO.setRemark(StringUtil.nvl(map.get("COMMENTS"), ""));
                        stlDTO.setGl_date(Formatter.parseToDate(map.get("GL_DATE")));
                        krw_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_KRW"), "0.0"))), 4);
                        usd_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_USD"), "0.0"))), 4);
                        stlDTO.setExchange_rate_krw(Double.valueOf(krw_rate));
                        stlDTO.setExchange_rate_usd(Double.valueOf(usd_rate));
                        //stlDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
                        stlDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXCHANGE_RATE_DATE_KRW")));    //RYU 2010.07.14
                    } else if ("".equals(openType)) {
                        glAcd = StringUtil.nvl(map.get("SEGMENT3"), "");
                        stlDTO.setCheck_item("0");
                        stlDTO.setStl_vsl_code(StringUtil.nvl(map.get("SEGMENT4"), ""));
                        stlDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("SEGMENT5"), "0")));
                        stlDTO.setCurrency_code(StringUtil.nvl(map.get("CURRENCY_CODE"), ""));
                        stlDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("ENTERED_AMT"), "0.0")));
                        stlDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("USD_AMT"), "0.0")));
                        stlDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("WON_AMT"), "0.0")));
                        stlDTO.setOp_team_code(StringUtil.nvl(map.get("SEGMENT2"), ""));
                        stlDTO.setStl_port_code(StringUtil.nvl(map.get("PORT_CODE"), ""));
                        stlDTO.setStl_acc_name(StringUtil.nvl(map.get("CUSTOMER_NAME"), ""));
                        stlDTO.setStl_acc_code(StringUtil.nvl(map.get("CUSTOMER_UNIQUE_ID"), ""));
                        stlDTO.setSlip_no(StringUtil.nvl(map.get("INVOICE_NUMBER"), ""));
                        stlDTO.setGl_acct(glAcd);
                        stlDTO.setRemark(StringUtil.nvl(map.get("COMMENTS"), ""));
                        stlDTO.setGl_date(Formatter.parseToDate(map.get("GL_DATE")));
                        krw_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_KRW"), "0.0"))), 4);
                        usd_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_USD"), "0.0"))), 4);
                        stlDTO.setExchange_rate_krw(Double.valueOf(krw_rate));
                        stlDTO.setExchange_rate_usd(Double.valueOf(usd_rate));
                        //stlDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
                        stlDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXCHANGE_RATE_DATE_KRW"))); // RYU 2010.07.14
                    }
                    result.add(stlDTO);
                }
            }
        }
        return result;
    }

    /**
     * <p>
     * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 Advanced Received정보을 읽어온다.
     */
    public Collection saAdvancedReceiveSearch(String fromDate, String toDate, String accCode) {
        Collection result = new ArrayList<>();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("fromDate", Formatter.nullTrim(fromDate));
        paramMap.put("toDate", Formatter.nullTrim(toDate));
        paramMap.put("accCode", Formatter.nullTrim(accCode));

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saAdvancedReceiveSearch", paramMap);
        OTCSaAdvancedDTO advDTO = null;
        double krw_rate = 0;
        double usd_rate = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                advDTO = new OTCSaAdvancedDTO();
                advDTO.setVsl(StringUtil.nvl(map.get("SEGMENT4"), ""));
                advDTO.setVoy(StringUtil.nvl(map.get("SEGMENT5"), ""));
                advDTO.setCurrency_code(StringUtil.nvl(map.get("CURRENCY_CODE"), ""));
                advDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("ENTERED_AMT"), "0.0")));
                advDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("USD_AMT"), "0.0")));
                advDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("WON_AMT"), "0.0")));
                advDTO.setDept(StringUtil.nvl(map.get("SEGMENT2"), ""));
                advDTO.setPort(StringUtil.nvl(map.get("PORT_CODE"), ""));
                advDTO.setAcc_name(StringUtil.nvl(map.get("VENDOR_NAME"), ""));
                advDTO.setAcc_code(StringUtil.nvl(map.get("CUSTOMER_UNIQUE_ID"), ""));
                advDTO.setSlip_no(StringUtil.nvl(map.get("INVOICE_NUMBER"), ""));
                advDTO.setGl_account(StringUtil.nvl(map.get("SEGMENT3"), ""));
                advDTO.setRemark(StringUtil.nvl(map.get("COMMENTS"), ""));
                advDTO.setGl_date(Formatter.parseToDate(map.get("GL_DATE")));
                krw_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_KRW"), "0.0"))), 4);
                usd_rate = Formatter.round(Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("EXCHANGE_RATE_USD"), "0.0"))), 4);
                advDTO.setExchange_rate_krw(Double.valueOf(krw_rate));
                advDTO.setExchange_rate_usd(Double.valueOf(usd_rate));
                //advDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
                advDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("EXCHANGE_RATE_DATE_KRW")));  //RYU
                result.add(advDTO);
            }
        }
        return result;
    }

    /**
     * <p>
     * 설명: 입력조건에 해당하는 sa 정보의 Balance check 정보를 읽어온다 chtInOutCOde : O, T, R
     */
    public Collection saBalanceCheckSearch(String vslCode, Long voyNo, String chtInOutCode, Long stepNo, String cntr_no) {
        Collection result = new ArrayList<>();
        Collection drs = new ArrayList<>();
        Collection crs = new ArrayList<>();
        // long bank_acc_id = 0;
        // String bank_acc_desc = "";
        long sa_no = 0;
        //String crItem = "		 select  b.trsact_code,  c.trsact_name ,b.vat_flag, ";
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
        // WithHoldingTaxInvoice 추가 : 소득세(M001), 주민세(M002)
        //crItem = crItem + "	and ((b.trsact_code between 'A001' and 'H008') OR b.trsact_code in ('M001','M002')) ";
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        //crItem = crItem + "	order by b.trsact_code ";
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
        //ITEM 나오는 순서 조정..
        //String crItemAC = " select   b.trsact_code,  c.trsact_name ,b.vat_flag, b.remark,";
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
        //crItemAC = crItemAC + "	and  b.trsact_code IN ('I001', 'I002','I003','I004','I005','I006','I007', 'I008','I009','I010','I011','I012','I013','I014','I015','I016','I017','I018','I019','I020','I021','I022','I023','I024','I025','I026','I027','I028','I029','I030','I031','I032','I033','I034','I035','I036','I037','I038','I039','I040','I041','I042','I043','I044','I045','I046','I047','I048','I049','I050','I051','I052','I053','I054','J001','J002','J003','J004','J005','J006','J007','J008','J009','J010','K001','K002','K003','K004','K005','K006','M005','M006','M007','M008','N001','N002') ";
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        //crItemAC = crItemAC + "	order by b.trsact_code";
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
        //ITEM 나오는 순서 조정..
        //String drItem = "			 select 	b.trsact_code,  c.trsact_name ,b.vat_flag, ";
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
        // WithHoldingTaxInvoice 추가 : M004(소득세+주민세=지급운임기타)
        //drItem = drItem + "		 		and ((b.trsact_code between 'A001' and 'H008') OR b.trsact_code = 'M004') ";
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        ///drItem = drItem + "	            order by b.trsact_code ";
        // 결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
        // ITEM 나오는 순서 조정..
        //String drItemAC = "			 select  b.trsact_code,  c.trsact_name ,b.vat_flag, b.remark, ";
        //drItemAC = drItemAC + "		 		and  b.trsact_code IN ('I001', 'I002','I003','I004','I005','I006','I007', 'I008','I009','I010','I011','I012','I013','I014','I015','I016','I017','I018','I019','I020','I021','I022','I023','I024','I025','I026','I027','I028','I029','I030','I031','I032','I033','I034','I035','I036','I037','I038','I039','I040','I041','I042','I043','I044','I045','I046','I047','I048','I049','I050','I051','I052','I053','I054','J001','J002','J003','J004','J005','J006','J007','J008','J009','J010','K001','K002','K003','K004','K005','K006','M005','M006','M007','M008','N001','N002') ";
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        //drItemAC = drItemAC + "	            order by b.trsact_code ";
        // 결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        // ITEM 나오는 순서 조정..
        //saHead = saHead + "					 ( select income_rate from otc_wth_tax_m where nat_code = acc_nation_func(a.cntr_acc_code) ) income_tax_rate,	"; //wth_tax 추가
        // 수정,,(bbc 선박 ) - hijang
        //saHead = saHead + "                  nvl( (select income_rate from OTC_WITHOLD_VSL_INFO where vsl_code = a.vsl_code ),    	";
        //saHead = saHead + "                         (select income_rate from otc_wth_tax_m where nat_code = acc_nation_func(a.cntr_acc_code)) ) income_tax_rate,	  	";
        // 수정,,(bbc 선박 ) - hijang(2012.04.19)
        //saHead = saHead + "                GET_WTH_TAX_RATE(a.cntr_acc_code, a.wth_flag) as income_tax_rate,	  	";
        // debit의 item을 구함 ============================================
        OTCBalanceCheckDTO balDTO = null;
        OTCBalanceHeadDTO bhDTO = new OTCBalanceHeadDTO();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", voyNo);
        paramMap.put("chtInOutCode", chtInOutCode);
        paramMap.put("stepNo", stepNo);
        paramMap.put("cntr_no", cntr_no);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBalanceCheckSearch", paramMap);
        String io = "";
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                sa_no = StringUtil.toLong((String) map.get("sa_no"), 0L);
                bhDTO.setSa_no(Formatter.nullLong(StringUtil.nvl(map.get("sa_no"), "0")));
                bhDTO.setOp_team_code(StringUtil.nvl(map.get("op_team_code")));
                bhDTO.setOp_team_name(StringUtil.nvl(map.get("team_name")));
                bhDTO.setCntr_acc_code(StringUtil.nvl(map.get("cntr_acc_code")));
                bhDTO.setCntr_acc_name(StringUtil.nvl(map.get("acc_name")));
                bhDTO.setNat_code(StringUtil.nvl(map.get("nat_code")));
                bhDTO.setVsl_code(StringUtil.nvl(map.get("vsl_code")));
                bhDTO.setVsl_name(StringUtil.nvl(map.get("vsl_name")));
                bhDTO.setProcess_sts_flag(StringUtil.nvl(map.get("process_sts_flag"), ""));
                bhDTO.setPosting_date(Formatter.parseToDate(map.get("posting_date")));
                io = String.valueOf(map.get("cht_in_out_code"));
                if ("T".equals(io)) {
                    bhDTO.setCht_in_out_name("T/C IN");
                } else {
                    bhDTO.setCht_in_out_name("T/C OUT");
                }
                bhDTO.setCht_in_out_code(StringUtil.nvl(map.get("cht_in_out_code")));
                bhDTO.setVoy_no(Formatter.nullLong(StringUtil.nvl(map.get("voy_no"), "0")));
                bhDTO.setStep_no(Formatter.nullLong(StringUtil.nvl(map.get("step_no"), "0")));
                bhDTO.setCntr_no(StringUtil.nvl(map.get("cntr_no")));
                //용선 원천 징수에 관련된 param 추가
                bhDTO.setNat_name(StringUtil.nvl(map.get("nat_name")));
                bhDTO.setLoc_exc_rate(Double.valueOf(StringUtil.nvl(map.get("loc_exc_rate"), "0.0")));
                bhDTO.setWth_flag(StringUtil.nvl(map.get("wth_flag")));
                bhDTO.setIncome_tax_rate(Double.valueOf(StringUtil.nvl(map.get("income_tax_rate"), "0.0")));
                bhDTO.setExc_date(Formatter.parseToDate(map.get("exc_date")));
                bhDTO.setExc_rate_type(StringUtil.nvl(map.get("exc_rate_type")));
                bhDTO.setUsd_exc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_exc_rate"), "0.0")));
                bhDTO.setUsd_loc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_loc_rate"), "0.0")));
                bhDTO.setCurcy_code(StringUtil.nvl(map.get("curcy_code")));
                bhDTO.setCancel_flag(StringUtil.nvl(map.get("cancel_flag")));
                //150519 GYJ
                bhDTO.setBenef_acc_code(StringUtil.nvl(map.get("benef_acc_code")));
            }
        }
        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("sa_no", sa_no);
        paramMap1.put("chtInOutCode", chtInOutCode);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saBalanceCheckSearch1", paramMap1);
        int row = 0;
        String vat_flag = "";
        String trsact_name = "";
        String trsact_code = "";
        double loc_amt = 0;
        double won_amt = 0;
        double vat_loc_amt = 0;
        double vat_won_amt = 0;
        double dr_tot_loc_amt = 0;
        double dr_tot_won_amt = 0;
        double cr_tot_loc_amt = 0;
        double cr_tot_won_amt = 0;
        Collection debit = new ArrayList<>();
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap1) {
                balDTO = new OTCBalanceCheckDTO();
                vat_flag = Formatter.nullTrim(String.valueOf(map.get("vat_flag")));
                trsact_name = Formatter.nullTrim(String.valueOf(map.get("trsact_name")));
                trsact_code = Formatter.nullTrim(String.valueOf(map.get("trsact_code")));
                if ("Y".equals(vat_flag)) {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_vat_sa_amt"), "0.0")));
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_vat_sa_amt"), "0.0")));
                } else {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = 0;
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = 0;
                }
                balDTO.setDebit_item_code(trsact_code);
                balDTO.setDebit_item(trsact_name);
                balDTO.setDebit_loc(Double.valueOf(loc_amt));
                balDTO.setDebit_won(Double.valueOf(won_amt));
                debit.add(balDTO);
                // kgw 20080729 (1)
                //				if ("Y".equals(vat_flag) && vat_won_amt > 0)
                if ("Y".equals(vat_flag)) {
                    balDTO = new OTCBalanceCheckDTO();
                    trsact_name = trsact_name + " " + "VAT";
                    balDTO.setDebit_item_code(trsact_code);
                    balDTO.setDebit_item(trsact_name);
                    balDTO.setDebit_loc(Double.valueOf(vat_loc_amt));
                    balDTO.setDebit_won(Double.valueOf(vat_won_amt));
                    debit.add(balDTO);
                }
                row = row + 1;
            }
        }

        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("sa_no", sa_no);
        paramMap2.put("chtInOutCode", chtInOutCode);

        List<Map<String, Object>> listMap2 = uxbDAO.select("OTCSADetail.saBalanceCheckSearch2", paramMap2);
        if (listMap2 != null && !listMap2.isEmpty()) {
            for (Map<String, Object> map : listMap2) {
                balDTO = new OTCBalanceCheckDTO();
                vat_flag = Formatter.nullTrim(String.valueOf(map.get("vat_flag")));
                trsact_name = Formatter.nullTrim(String.valueOf(map.get("trsact_name")));
                trsact_code = Formatter.nullTrim(String.valueOf(map.get("trsact_code")));
                if ("Y".equals(vat_flag)) {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_vat_sa_amt"), "0.0")));
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_vat_sa_amt"), "0.0")));
                } else {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = 0;
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = 0;
                }
                balDTO.setDebit_item_code(trsact_code);
                if (!"".equals(Formatter.nullTrim(String.valueOf(map.get("remark"))))) {
                    balDTO.setDebit_item(trsact_name.concat("  :  ").concat(Formatter.nullTrim(String.valueOf(map.get("remark")))));
                } else {
                    balDTO.setDebit_item(trsact_name);
                }
                balDTO.setDebit_loc(Double.valueOf(loc_amt));
                balDTO.setDebit_won(Double.valueOf(won_amt));
                debit.add(balDTO);
                //				 kgw 20080729 (2)
                //				if ("Y".equals(vat_flag) && vat_won_amt > 0)
                if ("Y".equals(vat_flag)) {
                    balDTO = new OTCBalanceCheckDTO();
                    trsact_name = trsact_name + " " + "VAT";
                    balDTO.setDebit_item_code(trsact_code);
                    balDTO.setDebit_item(trsact_name);
                    balDTO.setDebit_loc(Double.valueOf(vat_loc_amt));
                    balDTO.setDebit_won(Double.valueOf(vat_won_amt));
                    debit.add(balDTO);
                }
                row = row + 1;
            }
        }

        Map<String, Object> paramMap3 = new HashMap<>();
        paramMap3.put("sa_no", sa_no);
        paramMap3.put("chtInOutCode", chtInOutCode);

        List<Map<String, Object>> listMap3 = uxbDAO.select("OTCSADetail.saBalanceCheckSearch3", paramMap3);
        if (listMap3 != null && !listMap3.isEmpty()) {
            for (Map<String, Object> map : listMap3) {
                balDTO = new OTCBalanceCheckDTO();
                vat_flag = Formatter.nullTrim(String.valueOf(map.get("vat_flag")));
                trsact_name = Formatter.nullTrim(String.valueOf(map.get("trsact_name")));
                trsact_code = Formatter.nullTrim(String.valueOf(map.get("trsact_code")));
                if ("Y".equals(vat_flag)) {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_vat_sa_amt"), "0.0")));
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_vat_sa_amt"), "0.0")));
                } else {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = 0;
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = 0;
                }
                balDTO.setDebit_item_code(trsact_code);
                balDTO.setDebit_item(trsact_name);
                balDTO.setDebit_loc(Double.valueOf(loc_amt));
                balDTO.setDebit_won(Double.valueOf(won_amt));
                debit.add(balDTO);
                //				 kgw 20080729 (3)
                //				if ("Y".equals(vat_flag) && vat_won_amt > 0)
                if ("Y".equals(vat_flag)) {
                    balDTO = new OTCBalanceCheckDTO();
                    trsact_name = trsact_name + " " + "VAT";
                    balDTO.setDebit_item_code(trsact_code);
                    balDTO.setDebit_item(trsact_name);
                    balDTO.setDebit_loc(Double.valueOf(vat_loc_amt));
                    balDTO.setDebit_won(Double.valueOf(vat_won_amt));
                    debit.add(balDTO);
                }
                row = row + 1;
            }
        }
        drs.add(debit);
        // debit 총합을 구함 ============================================

        Map<String, Object> paramMap4 = new HashMap<>();
        paramMap4.put("sa_no", sa_no);
        paramMap4.put("chtInOutCode", chtInOutCode);

        List<Map<String, Object>> listMap4 = uxbDAO.select("OTCSADetail.saBalanceCheckSearch4", paramMap4);
        balDTO = null;
        if (listMap4 != null && !listMap4.isEmpty()) {
            for (Map<String, Object> map : listMap4) {
                balDTO = new OTCBalanceCheckDTO();
                loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_amt"), "0.0")));
                trsact_name = "";
                balDTO.setDebit_item(trsact_name);
                balDTO.setDebit_loc(Double.valueOf(loc_amt));
                balDTO.setDebit_won(Double.valueOf(won_amt));
                dr_tot_loc_amt = dr_tot_loc_amt + loc_amt;
                log.debug("---차변 TOTAL(drTot) 값  dr_tot_loc_amt : " + dr_tot_loc_amt);
                log.debug("---차변 TOTAL(drTot)  값  loc_amt : " + loc_amt);
                dr_tot_won_amt = dr_tot_won_amt + won_amt;
            }
        }
        drs.add(balDTO);
        // debit balance 총합을 구함 ============================================
        int j = 1;

        Map<String, Object> paramMap5 = new HashMap<>();
        paramMap5.put("sa_no", sa_no);
        paramMap5.put("chtInOutCode", chtInOutCode);

        List<Map<String, Object>> listMap5 = uxbDAO.select("OTCSADetail.saBalanceCheckSearch5", paramMap5);
        balDTO = null;
        if (listMap5 != null && !listMap5.isEmpty()) {
            for (Map<String, Object> map : listMap5) {
                balDTO = new OTCBalanceCheckDTO();
                loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_amt"), "0.0")));
                // bank_acc_id = String.valueOf(map.get("bank_acc_id"));
                // bank_acc_desc =
                // Formatter.nullTrim(String.valueOf(map.get("bank_acc_desc")));
                trsact_name = "";
                balDTO.setDebit_item(trsact_name);
                balDTO.setDebit_loc(Double.valueOf(loc_amt));
                balDTO.setDebit_won(Double.valueOf(won_amt));
                if (loc_amt > 0) {
                    bhDTO.setApAr("AR");
                }
                dr_tot_loc_amt = dr_tot_loc_amt + loc_amt;
                log.debug("---차변 BALANCE값(drBal)  dr_tot_loc_amt : " + dr_tot_loc_amt);
                log.debug("---차변 BALANCE값(drBal)  loc_amt : " + loc_amt);
                dr_tot_won_amt = dr_tot_won_amt + won_amt;
                bhDTO.setDue_date(Formatter.parseToDate(map.get("due_date")));
                bhDTO.setPymt_term(StringUtil.nvl(map.get("pymt_term")));
                bhDTO.setTerms_date(Formatter.parseToDate(map.get("terms_date")));
                bhDTO.setPymt_hold_flag(StringUtil.nvl(map.get("pymt_hold_flag")));
                bhDTO.setPymt_meth(StringUtil.nvl(map.get("pymt_meth")));
                bhDTO.setPymt_term(StringUtil.nvl(map.get("pymt_term")));
                bhDTO.setBank_acc_id(Formatter.nullLong(StringUtil.nvl(map.get("bank_acc_id"), "0")));
                bhDTO.setBank_acc_desc(StringUtil.nvl(map.get("bank_acc_desc")));
            }
        }
        drs.add(balDTO);
        balDTO = new OTCBalanceCheckDTO();
        balDTO.setDr_tot_loc_amt(Double.valueOf(dr_tot_loc_amt));
        balDTO.setDr_tot_won_amt(Double.valueOf(dr_tot_won_amt));
        drs.add(balDTO);
        // credit item을 구함 대변

        Map<String, Object> paramMap6 = new HashMap<>();
        paramMap6.put("sa_no", sa_no);
        paramMap6.put("chtInOutCode", chtInOutCode);

        List<Map<String, Object>> listMap6 = uxbDAO.select("OTCSADetail.saBalanceCheckSearch6", paramMap6);
        balDTO = null;
        Collection credit = new ArrayList<>();
        io = "";
        if (listMap6 != null && !listMap6.isEmpty()) {
            for (Map<String, Object> map : listMap6) {
                balDTO = new OTCBalanceCheckDTO();
                vat_flag = StringUtil.nvl(map.get("vat_flag"), "");
                trsact_name = StringUtil.nvl(map.get("trsact_name"), "");
                trsact_code = StringUtil.nvl(map.get("trsact_code"), "");
                if ("Y".equals(vat_flag)) {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_vat_sa_amt"), "0.0")));
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_vat_sa_amt"), "0.0")));
                } else {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = 0;
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = 0;
                }
                balDTO.setCredit_item_code(trsact_code);
                balDTO.setCredit_item(trsact_name);
                balDTO.setCredit_loc(Double.valueOf(loc_amt));
                balDTO.setCredit_won(Double.valueOf(won_amt));
                credit.add(balDTO);
                //				 kgw 20080729 (4)
                //				if ("Y".equals(vat_flag) && vat_won_amt > 0)
                if ("Y".equals(vat_flag)) {
                    balDTO = new OTCBalanceCheckDTO();
                    trsact_name = trsact_name + " " + "VAT";
                    balDTO.setCredit_item_code(trsact_code);
                    balDTO.setCredit_item(trsact_name);
                    balDTO.setCredit_loc(Double.valueOf(vat_loc_amt));
                    balDTO.setCredit_won(Double.valueOf(vat_won_amt));
                    credit.add(balDTO);
                }
            }
        }

        Map<String, Object> paramMap7 = new HashMap<>();
        paramMap7.put("sa_no", sa_no);
        paramMap7.put("chtInOutCode", chtInOutCode);

        List<Map<String, Object>> listMap7 = uxbDAO.select("OTCSADetail.saBalanceCheckSearch7", paramMap7);
        io = "";
        if (listMap7 != null && !listMap7.isEmpty()) {
            for (Map<String, Object> map : listMap7) {
                balDTO = new OTCBalanceCheckDTO();
                vat_flag = Formatter.nullTrim(String.valueOf(map.get("vat_flag")));
                trsact_name = Formatter.nullTrim(String.valueOf(map.get("trsact_name")));
                trsact_code = Formatter.nullTrim(String.valueOf(map.get("trsact_code")));
                if ("Y".equals(vat_flag)) {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_vat_sa_amt"), "0.0")));
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_vat_sa_amt"), "0.0")));
                } else {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = 0;
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = 0;
                }
                balDTO.setCredit_item_code(trsact_code);
                if (!"".equals(Formatter.nullTrim(String.valueOf(map.get("remark"))))) {
                    balDTO.setCredit_item(trsact_name.concat("  :  ").concat(Formatter.nullTrim(String.valueOf(map.get("remark")))));
                } else {
                    balDTO.setCredit_item(trsact_name);
                }
                balDTO.setCredit_loc(Double.valueOf(loc_amt));
                balDTO.setCredit_won(Double.valueOf(won_amt));
                credit.add(balDTO);
                //				 kgw 20080729 (5)
                //				if ("Y".equals(vat_flag) && vat_won_amt > 0)
                if ("Y".equals(vat_flag)) {
                    balDTO = new OTCBalanceCheckDTO();
                    trsact_name = trsact_name + " " + "VAT";
                    balDTO.setCredit_item_code(trsact_code);
                    balDTO.setCredit_item(trsact_name);
                    balDTO.setCredit_loc(Double.valueOf(vat_loc_amt));
                    balDTO.setCredit_won(Double.valueOf(vat_won_amt));
                    credit.add(balDTO);
                }
            }
        }

        Map<String, Object> paramMap8 = new HashMap<>();
        paramMap8.put("sa_no", sa_no);
        paramMap8.put("chtInOutCode", chtInOutCode);

        List<Map<String, Object>> listMap8 = uxbDAO.select("OTCSADetail.saBalanceCheckSearch8", paramMap8);
        io = "";
        if (listMap8 != null && !listMap8.isEmpty()) {
            for (Map<String, Object> map : listMap8) {
                balDTO = new OTCBalanceCheckDTO();
                vat_flag = Formatter.nullTrim(String.valueOf(map.get("vat_flag")));
                trsact_name = Formatter.nullTrim(String.valueOf(map.get("trsact_name")));
                trsact_code = Formatter.nullTrim(String.valueOf(map.get("trsact_code")));
                if ("Y".equals(vat_flag)) {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_vat_sa_amt"), "0.0")));
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_vat_sa_amt"), "0.0")));
                } else {
                    loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                    vat_loc_amt = 0;
                    won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                    vat_won_amt = 0;
                }
                balDTO.setCredit_item_code(trsact_code);
                balDTO.setCredit_item(trsact_name);
                balDTO.setCredit_loc(Double.valueOf(loc_amt));
                balDTO.setCredit_won(Double.valueOf(won_amt));
                credit.add(balDTO);
                //				 kgw 20080729 (6)
                //				if ("Y".equals(vat_flag) && vat_won_amt > 0)
                if ("Y".equals(vat_flag)) {
                    balDTO = new OTCBalanceCheckDTO();
                    trsact_name = trsact_name + " " + "VAT";
                    balDTO.setCredit_item_code(trsact_code);
                    balDTO.setCredit_item(trsact_name);
                    balDTO.setCredit_loc(Double.valueOf(vat_loc_amt));
                    balDTO.setCredit_won(Double.valueOf(vat_won_amt));
                    credit.add(balDTO);
                }
            }
        }
        crs.add(credit);
        // credit 총합을 구함 ============================================

        Map<String, Object> paramMap9 = new HashMap<>();
        paramMap9.put("sa_no", sa_no);
        paramMap9.put("chtInOutCode", chtInOutCode);

        List<Map<String, Object>> listMap9 = uxbDAO.select("OTCSADetail.saBalanceCheckSearch9", paramMap9);
        balDTO = null;
        if (listMap9 != null && !listMap9.isEmpty()) {
            for (Map<String, Object> map : listMap9) {
                balDTO = new OTCBalanceCheckDTO();
                loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_amt"), "0.0")));
                trsact_name = "";
                balDTO.setCredit_item(trsact_name);
                balDTO.setCredit_loc(Double.valueOf(loc_amt));
                balDTO.setCredit_won(Double.valueOf(won_amt));
                cr_tot_loc_amt = cr_tot_loc_amt + loc_amt;
                log.debug("---대변 total값(crTot)  dr_tot_loc_amt : " + cr_tot_loc_amt);
                log.debug("---대변 total값(crTot)  loc_amt : " + loc_amt);
                cr_tot_won_amt = cr_tot_won_amt + won_amt;
            }
        }
        crs.add(balDTO);
        // credit balance 총합을 구함
        // ============================================

        Map<String, Object> paramMap10 = new HashMap<>();
        paramMap10.put("sa_no", sa_no);
        paramMap10.put("chtInOutCode", chtInOutCode);

        List<Map<String, Object>> listMap10 = uxbDAO.select("OTCSADetail.saBalanceCheckSearch10", paramMap10);
        balDTO = null;
        if (listMap10 != null && !listMap10.isEmpty()) {
            for (Map<String, Object> map : listMap10) {
                balDTO = new OTCBalanceCheckDTO();
                loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_amt"), "0.0")));
                trsact_name = "";
                balDTO.setCredit_item(trsact_name);
                balDTO.setCredit_loc(Double.valueOf(loc_amt));
                balDTO.setCredit_won(Double.valueOf(won_amt));
                if (loc_amt > 0) {
                    bhDTO.setApAr("AP");
                }
                cr_tot_loc_amt = cr_tot_loc_amt + loc_amt;
                log.debug("---대변 BALANCE값(crBal)  cr_tot_loc_amt : " + cr_tot_loc_amt);
                log.debug("---대변 BALANCE값(crBal)   loc_amt : " + loc_amt);
                cr_tot_won_amt = cr_tot_won_amt + won_amt;
                bhDTO.setDue_date(Formatter.parseToDate(map.get("due_date")));
                bhDTO.setPymt_term(StringUtil.nvl(map.get("pymt_term")));
                bhDTO.setTerms_date(Formatter.parseToDate(map.get("terms_date")));
                bhDTO.setPymt_hold_flag(StringUtil.nvl(map.get("pymt_hold_flag")));
                bhDTO.setPymt_meth(StringUtil.nvl(map.get("pymt_meth")));
                bhDTO.setPymt_term(StringUtil.nvl(map.get("pymt_term")));
                bhDTO.setBank_acc_id(Formatter.nullLong(StringUtil.nvl(map.get("bank_acc_id"), "0")));
                bhDTO.setBank_acc_desc(StringUtil.nvl(map.get("bank_acc_desc")));
                //140314 GYJ
                bhDTO.setCourt_admit_no(StringUtil.nvl(map.get("court_admit_no")));
                bhDTO.setCourt_flag(StringUtil.nvl(map.get("court_flag")));
            }
        }
        crs.add(balDTO);
        balDTO = new OTCBalanceCheckDTO();
        balDTO.setCr_tot_loc_amt(Double.valueOf(cr_tot_loc_amt));
        balDTO.setCr_tot_won_amt(Double.valueOf(cr_tot_won_amt));
        crs.add(balDTO);
        // 왼쪽 차변
        result.add(drs);
        // 오른쪽 대 변
        result.add(crs);
        // 윗쪽
        result.add(bhDTO);
        return result;
    }

    /**
     * <p>
     * 설명: 입력조건에 해당하는 sa 정보의 Balance 정보를 계산한다. chtInOutCOde : O, T, R
     */
    public OTCBalanceCheckDTO saBalanceCheckCalculation(Long saNo, String chtInOutCode) {
        OTCBalanceCheckDTO result = new OTCBalanceCheckDTO();
        // debit의 item을 구함 ============================================
        // OTCBalanceCheckDTO balDTO = null;
        // int row = 0;
        // String vat_flag = "";
        // String trsact_name = "";
        double cr_loc_amt = 0;
        double cr_won_amt = 0;
        double dr_loc_amt = 0;
        double dr_won_amt = 0;
        double loc_amt = 0;
        double won_amt = 0;
        // debit balance 총합을 구함 ============================================

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBalanceCheckCalculation", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                dr_loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                dr_won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_amt"), "0.0")));
            }
        }
        // credit 총합을 구함 ============================================

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", saNo);
        paramMap1.put("chtInOutCode", chtInOutCode);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saBalanceCheckCalculation1", paramMap1);
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                cr_loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                cr_won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_amt"), "0.0")));
            }
        }
        loc_amt = dr_loc_amt - cr_loc_amt;
        won_amt = dr_won_amt - cr_won_amt;
        if (loc_amt >= 0) {
            result.setBal_item_code("L001");
            result.setBal_loc(Double.valueOf(loc_amt));
            result.setBal_won(Double.valueOf(won_amt));
        } else {
            result.setBal_item_code("L002");
            loc_amt = loc_amt * -1;
            won_amt = won_amt * -1;
            result.setBal_loc(Double.valueOf(loc_amt));
            result.setBal_won(Double.valueOf(won_amt));
        }
        return result;
    }

    public double saDiffKrwAmtCalculation(Long saNo) {
        double result = 0.0;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saDiffKrwAmtCalculation", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                result = Formatter.nullDouble(StringUtil.nvl(map.get("diff_krw_amt"), "0"));
            }
        }
        log.debug("saDiffKrwAmtCalculation=" + result);
        return result;
    }

    /**
     * <p>
     * 설명: sa Balance Check Due Date Modify한다.
     */
    public String saBalanceDueDateModify(Long saNo, OTCBalanceHeadDTO infos) {
        UserDelegation userInfo = UserInfo.getUserInfo();
        String result = "";
        if (saNo != null) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("due_date", infos.getDue_date());
            paramMap.put("terms_date", infos.getTerms_date());
            paramMap.put("pymt_hold_flag", Formatter.nullTrim(infos.getPymt_hold_flag()));
            paramMap.put("bank_acc_id", Formatter.nullLong(StringUtil.nvl(infos.getBank_acc_id(), "0")));
            paramMap.put("bank_acc_desc", Formatter.nullTrim(infos.getBank_acc_desc()));
            paramMap.put("_sessionUserId", Formatter.nullTrim(userInfo.getUserId()));
            paramMap.put("saNo", saNo);
            // Query 가져오기
            // ps.setString(i++, Formatter.nullTrim(infos.getPymt_term()));

            uxbDAO.update("OTCSADetail.saBalanceDueDateModify", paramMap);
            result = "SUC-0600";
        }
        return result;
    }

    /**
     * <p>
     * 설명:sa open type 을 리턴하는 메소드이다.
     */
    public String saOpenType(String gl_accd) {
        String result = "";
        if ("210802".equals(Formatter.nullTrim(gl_accd)) || "210803".equals(Formatter.nullTrim(gl_accd)) || "210809".equals(Formatter.nullTrim(gl_accd))) {
            result = "OE";
        } else if ("210805".equals(Formatter.nullTrim(gl_accd))) {
            result = "RB";
        } else if ("210402".equals(Formatter.nullTrim(gl_accd)) || "210405".equals(Formatter.nullTrim(gl_accd)) || "210403".equals(Formatter.nullTrim(gl_accd)) || "210701".equals(Formatter.nullTrim(gl_accd)) || "210499".equals(Formatter.nullTrim(gl_accd))) {        //111013 GYJ (210499)기타영업미지급금 추가
            result = "AP";
        } else if ("110902".equals(Formatter.nullTrim(gl_accd)) || "110903".equals(Formatter.nullTrim(gl_accd)) || "110907".equals(Formatter.nullTrim(gl_accd)) || "110912".equals(Formatter.nullTrim(gl_accd)) || "110913".equals(Formatter.nullTrim(gl_accd))) {
            result = "AC";
        } else if ("110502".equals(Formatter.nullTrim(gl_accd)) || "110503".equals(Formatter.nullTrim(gl_accd)) || "110599".equals(Formatter.nullTrim(gl_accd))) {    //111013 GYJ (110599)영업미수-기타잔액 추가.
            result = "AR";
        }
        return result;
    }

    /**
     * <p>
     * 설명: sa Withholding Tax의 hirage값을 구한다. Register정보를 만든다.
     */
    public OTCSaOnHireDTO saDetailWithholdHireCal(Long saNo, Double usd_amt, Double won_amt) {
        OTCSaOnHireDTO result = new OTCSaOnHireDTO();
        if (saNo != null) {
            // Query 가져오기
            //sb.append("	SELECT SA_NO ,MIN(FROM_DATE) as from_date, MAX(TO_DATE) as to_date, SUM(NVL(SA_RATE_DUR,0)) as rate_dur, SUM(NVL(SA_RATE,0)) as rate FROM OTC_SA_DETAIL WHERE SA_NO = ? AND TRSACT_CODE ='A001' GROUP BY SA_NO ");
            //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("saNo", saNo);

            List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saDetailWithholdHireCal", paramMap);
            if (listMap != null && !listMap.isEmpty()) {
                for (Map<String, Object> map : listMap) {
                    result.setDay_hire(Double.valueOf(StringUtil.nvl(map.get("rate"), "0.0")));
                    result.setDur(Double.valueOf(StringUtil.nvl(map.get("rate_dur"), "0.0")));
                    result.setFrom_date(Formatter.parseToDate(map.get("from_date")));
                    result.setTo_date(Formatter.parseToDate(map.get("to_date")));
                }
            }
            result.setSa_no((double) saNo);
            result.setAmount_usd(usd_amt);
            result.setAmount_krw(won_amt);
        }
        return result;
    }

    /**
     * <p>
     * 설명:sa info 내역을 삭제하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA detail 테이블에 삭제할 시 발생하는 메소드를 리턴한다.
     * saHeadDelete 실행하다 발생하는 모든 Exception을 처리한다
     */
    public String saNoAllDelete(Long saNo) throws Exception {
        String result = "";
        StringBuilder sb = new StringBuilder();
        // Query 가져오기
        sb.append(" select sa_no from otc_sa_detail ");
        sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");
        commonDao.setObject(OTCSaDetailVO.class, sb.toString(), 4);
        result = "SUC-0600";
        return result;
    }

    public String saCbDetailDelete(Long saNo) throws Exception {
        String result = "";
        StringBuilder sb = new StringBuilder();
        // Query 가져오기
        sb.append(" select sa_no from otc_sa_cb_detail ");
        sb.append(" WHERE SA_NO = " + saNo + " ");
        commonDao.setObject(OTCSaCbDetailVO.class, sb.toString(), 4);
        result = "SUC-0600";
        return result;
    }

    /**
     * <p>
     * 설명:sa detail 내역을 삭제하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호 ,trsact code
     * @return msgCode String: SA HEAD 테이블에 삭제할 시 발생하는 메소드를 리턴한다.
     * Exception을 처리한다
     */
    public String saTrsactCodeDelete(Long saNo, String trsactCode) throws Exception {
        String result = "";
        StringBuilder sb = new StringBuilder();
        // Query 가져오기
        sb.append("	SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*    ");
        sb.append("	FROM OTC_SA_DETAIL V      ");
        sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");
        sb.append(" AND TRSACT_CODE = '" + trsactCode + "' ");
        commonDao.setObject(OTCSaDetailVO.class, sb.toString(), 4);
        result = "SUC-0600";
        return result;
    }

    /**
     * <p>
     * 설명:sa bunker init내역을 조회하는 메소드이다.
     *
     * @param vslCode : voyNo : chtInCode
     *                sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public OTCSaBunkerDTO saBunkerInitSelect(String vslCode, Long voyNo, String chtInCode) {
        OTCSaBunkerDTO result = null;
        // Query 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", voyNo);
        paramMap.put("chtInCode", chtInCode);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBunkerInitSelect", paramMap);
        result = new OTCSaBunkerDTO();
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                result.setBod_fo_qty(0.0);
                result.setBod_fo_price(Double.valueOf(StringUtil.nvl(map.get("FO_PRICE"), "0.0")));
                result.setBod_do_qty(0.0);
                result.setBod_do_price(Double.valueOf(StringUtil.nvl(map.get("DO_PRICE"), "0.0")));
                result.setBor_fo_qty(0.0);
                result.setBor_fo_price(Double.valueOf(StringUtil.nvl(map.get("FO_PRICE"), "0.0")));
                result.setBor_do_qty(0.0);
                result.setBor_do_price(Double.valueOf(StringUtil.nvl(map.get("DO_PRICE"), "0.0")));
            }
        }
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     *
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public Collection saOwnerACInitSearch(String vslCode, Long voyNo, String chtinCd, Double dayHires, Date fromHire, Date toHire) {
        Collection result = null;
        String brok11 = "";
        String brok12 = "";
        boolean broker_check = false;
        // **************************** Brokerage 가져오기 시작
        // **************************** //

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", Formatter.nullLong(voyNo));
        paramMap.put("chtinCd", chtinCd);
        List<Map<String, Object>> listMap;

        if (fromHire == null) {
            listMap = uxbDAO.select("OTCSADetail.saOwnerACInitSearch", paramMap);
        } else {
            listMap = uxbDAO.select("OTCSADetail.saOwnerACInitSearch1", paramMap);
        }
        // if (fromHire != null)
        // int i = 1;
        // ps.setString(i++, vslCode);
        // ps.setLong(i++, voyNo.longValue());
        // ps.setString(i++, chtinCd);
        // ps.setString(i++, vslCode);
        // ps.setLong(i++, voyNo.longValue());
        // ps.setString(i++, chtinCd);
        // ps.setString(i++, DateUtil.getShortTimeStampString2(fromHire));
        // if (toHire == null) toHire = fromHire;
        // ps.setString(i++, DateUtil.getShortTimeStampString2(toHire));
        //

        OTCSaBrokerageDTO brokDTO = new OTCSaBrokerageDTO();
        result = new ArrayList<>();
        double add_comm = 0;
        double fo_prc = 0;
        double do_prc = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                if (!"T".equals(chtinCd) && "KR".equals(StringUtil.nvl(map.get("BROK_NAT"), ""))) {
                } else {
                    broker_check = true;
                    brok11 = String.valueOf(map.get("BROK_ACC_CODE"));
                    brokDTO.setBroker(StringUtil.nvl(map.get("BROK_ACC_CODE")));
                    brokDTO.setBroker_name(StringUtil.nvl(map.get("BROK_ACC_NAME"), ""));
                    brokDTO.setBrokerage_krw(0.0);
                    brokDTO.setBrokerage_usd(0.0);
                    brokDTO.setComm(Double.valueOf(StringUtil.nvl(map.get("BROK_COMM_RATE"), "0.0")));
                    brokDTO.setRemark("");
                    brokDTO.setBrok_reserve_flag("N");
                }
                if (!"T".equals(chtinCd) && "KR".equals(StringUtil.nvl(map.get("BROK_NAT2"), ""))) {
                } else {
                    if (broker_check) {
                        brok12 = String.valueOf(map.get("BROK_ACC_CODE2"));
                        brokDTO.setBroker2(StringUtil.nvl(map.get("BROK_ACC_CODE2")));
                        brokDTO.setBroker_name2(StringUtil.nvl(map.get("BROK_ACC_NAME2"), ""));
                        brokDTO.setBrokerage_krw2(0.0);
                        brokDTO.setBrokerage_usd2(0.0);
                        brokDTO.setComm2(Double.valueOf(StringUtil.nvl(map.get("BROK_COMM_RATE2"), "0.0")));
                        brokDTO.setRemark2("");
                        brokDTO.setBrok_reserve_flag2("N");
                    } else {
                        brok11 = String.valueOf(map.get("BROK_ACC_CODE2"));
                        brokDTO.setBroker(StringUtil.nvl(map.get("BROK_ACC_CODE2")));
                        brokDTO.setBroker_name(StringUtil.nvl(map.get("BROK_ACC_NAME2"), ""));
                        brokDTO.setBrokerage_krw(0.0);
                        brokDTO.setBrokerage_usd(0.0);
                        brokDTO.setComm(Double.valueOf(StringUtil.nvl(map.get("BROK_COMM_RATE2"), "0.0")));
                        brokDTO.setRemark("");
                        brokDTO.setBrok_reserve_flag("N");
                    }
                }
                fo_prc = Formatter.nullDouble(StringUtil.nvl(map.get("FO_PRICE"), "0"));
                do_prc = Formatter.nullDouble(StringUtil.nvl(map.get("DO_PRICE"), "0"));
                if (fromHire != null) {
                    dayHires = Double.valueOf(StringUtil.nvl(map.get("DAY_HIRE"), "0.0"));
                    add_comm = Formatter.nullDouble(StringUtil.nvl(map.get("ADDR_COMM_RATE"), "0"));
                }
            }
        }
        brokDTO.setHire(0.0);
        brokDTO.setHire2(0.0);
        result.add(brokDTO);
        // **************************** Brokerage 가져오기 종료
        // **************************** //
        // **************************** Speed Claim 가져오기 시작
        // **************************** //
        Collection speeds = new ArrayList<>();
        OTCSaSpeedClaimDTO speedDTO = new OTCSaSpeedClaimDTO();
        speedDTO.setAdd_comm(Double.valueOf(add_comm));
        speedDTO.setFo_price(Double.valueOf(fo_prc));
        speedDTO.setDo_price(Double.valueOf(do_prc));
        speedDTO.setDay_hire(dayHires);
        speedDTO.setSpeed_claim_flag("R"); // Reserved
        if ("R".equals(Formatter.nullTrim(speedDTO.getSpeed_claim_flag()))) {
            speedDTO.setFactor(Double.valueOf(100 - Formatter.nullDouble(StringUtil.nvl(speedDTO.getAdd_comm(), "0"))));
        } else {
            speedDTO.setFactor(Double.valueOf(100));
        }
        speedDTO.setOrg_factor(Double.valueOf(100));
        speedDTO.setRsv_factor(Double.valueOf(100 - Formatter.nullDouble(StringUtil.nvl(speedDTO.getAdd_comm(), "0"))));
        speeds.add(speedDTO);
        result.add(speeds);
        // **************************** Speed Claim 가져오기 종료
        // **************************** //
        Collection acs = new ArrayList<>();
        result.add(acs);
        // **************************** Owner's A/C 가져오기 종료
        // **************************** //
        // **************************** bank info 가져오기 시작
        // **************************** //
        // DbWrap dbWrap = new DbWrap();
        StringBuilder sb8 = new StringBuilder();

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("brok", brok11);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saOwnerACInitSearch2", paramMap1);
        EARVendorBankAccountVDTO bankDTO = new EARVendorBankAccountVDTO();
        // 빈공백을 처음에 넣는다. 빈공백도 선택가능하다.
        bankDTO.setBank_acc_id(0L);
        bankDTO.setBank_acc_desc("");
        Collection bankInfo1 = new ArrayList<>();
        bankInfo1.add(bankDTO);
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap1) {
                bankDTO = new EARVendorBankAccountVDTO();
                bankDTO.setBank_acc_id(Formatter.nullLong(StringUtil.nvl(map.get("BANK_ACC_ID"), "0")));
                bankDTO.setBank_acc_desc(StringUtil.nvl(map.get("BANK_ACC_DESC"), ""));
                bankInfo1.add(bankDTO);
            }
        }
        result.add(bankInfo1);
        //rs2 = ps1.executeQuery();

        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("brok", brok12);

        List<Map<String, Object>> listMap2 = uxbDAO.select("OTCSADetail.saOwnerACInitSearch2", paramMap2);
        bankDTO = new EARVendorBankAccountVDTO();
        // 빈공백을 처음에 넣는다. 빈공백도 선택가능하다.
        bankDTO.setBank_acc_id(0L);
        bankDTO.setBank_acc_desc("");
        Collection bankInfo2 = new ArrayList<>();
        bankInfo1.add(bankDTO);
        //while (rs1.next())
        if (listMap2 != null && !listMap2.isEmpty()) {
            for (Map<String, Object> map : listMap2) {
                bankDTO = new EARVendorBankAccountVDTO();
				/*bankDTO.setBank_acc_id(Formatter.nullDouble(StringUtil.nvl(map.get("BANK_ACC_ID"), "0")));
				bankDTO.setBank_acc_desc(StringUtil.nvl(map.get("BANK_ACC_DESC"), ""));
				*/
                //1.8로 올리고 "결과 집합을 종료했음: next" 라는 에러가 나서 수정 220518
                bankDTO.setBank_acc_id(Formatter.nullLong(StringUtil.nvl(map.get("BANK_ACC_ID"), "0")));
                bankDTO.setBank_acc_desc(StringUtil.nvl(map.get("BANK_ACC_DESC"), ""));
                bankInfo2.add(bankDTO);
            }
        }
        result.add(bankInfo2);
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     * <p>
     * sa 번호
     *
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saOffHireSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public Collection saOffHireInitSelect(String vslCode, Long voyNo, String chtInCode, Date fromHire, Date toHire) {
        Collection result = new ArrayList<>();
        // **************************** NegoAmount/Compensation 가져오기 시작
        // **************************** //
        OTCSaOffHireNegoDTO negoDTO = new OTCSaOffHireNegoDTO();
        result.add(negoDTO);
        // **************************** NegoAmount/Compensation 가져오기 종료
        // **************************** //
        // **************************** Off Hire 가져오기 시작
        // **************************** //

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", voyNo);
        paramMap.put("chtInCode", chtInCode);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saOffHireInitSelect", paramMap);
        OTCSaOffHireDTO offDTO = null;
        Collection offs = new ArrayList<>();
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                offDTO = new OTCSaOffHireDTO();
                if ("".equals(Formatter.nullTrim(offDTO.getStl_flag()))) {
                    offDTO.setStl_flag("A");
                }
                if ("".equals(Formatter.nullTrim(offDTO.getOwn_spd_clm_flag()))) {
                    offDTO.setOwn_spd_clm_flag("N");
                }  // Actual(Net)
                offDTO.setDay_hire(Double.valueOf(StringUtil.nvl(map.get("DAY_HIRE"), "0.0")));
                offDTO.setAdd_comm(Double.valueOf(StringUtil.nvl(map.get("ADDR_COMM_RATE"), "0.0")));
                offDTO.setFo_price(Double.valueOf(StringUtil.nvl(map.get("FO_PRICE"), "0.0")));
                offDTO.setDo_price(Double.valueOf(StringUtil.nvl(map.get("DO_PRICE"), "0.0")));
                offDTO.setFo_idle(Double.valueOf(StringUtil.nvl(map.get("FO_IDLE"), "0.0")));
                offDTO.setDo_idle(Double.valueOf(StringUtil.nvl(map.get("DO_IDLE"), "0.0")));
                offDTO.setFactor(Double.valueOf(100));
                offs.add(offDTO);
                offDTO = new OTCSaOffHireDTO();
                if ("".equals(Formatter.nullTrim(offDTO.getStl_flag()))) {
                    offDTO.setStl_flag("A");
                }
                if ("".equals(Formatter.nullTrim(offDTO.getOwn_spd_clm_flag()))) {
                    offDTO.setOwn_spd_clm_flag("N");
                }  // Actual(Net)
                offDTO.setDay_hire(Double.valueOf(StringUtil.nvl(map.get("DAY_HIRE"), "0.0")));
                offDTO.setAdd_comm(Double.valueOf(StringUtil.nvl(map.get("ADDR_COMM_RATE"), "0.0")));
                offDTO.setFo_price(Double.valueOf(StringUtil.nvl(map.get("FO_PRICE"), "0.0")));
                offDTO.setDo_price(Double.valueOf(StringUtil.nvl(map.get("DO_PRICE"), "0.0")));
                offDTO.setFo_idle(Double.valueOf(StringUtil.nvl(map.get("FO_IDLE"), "0.0")));
                offDTO.setDo_idle(Double.valueOf(StringUtil.nvl(map.get("DO_IDLE"), "0.0")));
                offDTO.setFactor(Double.valueOf(100));
                offs.add(offDTO);
            } // while
        } // while
        result.add(offs);
        // **************************** Off Hire 가져오기 종료
        // **************************** //
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail 내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public Collection saDetailBySaNoSearch(Long saNo) {
        Collection result = null;
        StringBuilder sb = new StringBuilder();
        // Query 가져오기
        sb.append("	select a.*,trsact_name_func('SOMO',b.cht_in_out_code,a.trsact_code) as trsact_name, b.posting_date, b.cht_in_out_code, b.op_team_code from otc_sa_detail a, otc_sa_head b where a.sa_no = b.sa_no  ");
        sb.append(" AND A.SA_NO = " + saNo + " ");
        log.debug("saDetailBySaNoSearch : " + sb.toString());
        //result = commonDao.getObjects(conn, OTCSaDetailDTO.class, sb.toString());
        result = commonDao.getObjects(OTCSaCbDetailDTO.class, sb.toString());    //향후 무조건 CbDetailDTO 사용할 것. 150126 GYJ
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Owner Settle역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     */
    public Collection saOwnerSettleRsvUnSubmitSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo, String stlFlag, String processFlag) {
        Collection result = new ArrayList<>();
        Collection rev = new ArrayList<>();
        Collection act = new ArrayList<>();
        Collection brk = new ArrayList<>();
        // String stlFlagCk = "";
        // String sExist = "";
        //String invoice = "	 select d.* , a.stl_cntr_acc_code,c.op_team_code, c.cntr_team_code,    ";

        // **************************** Reserved(Owners' Exp) 가져오기 시작
        // **************************** //

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", voyNo);
        paramMap.put("chtInOutCode", chtInOutCode);
        paramMap.put("saNo", Formatter.nullLong(saNo));
        paramMap.put("stlFlag", stlFlag);
        paramMap.put("processFlag", processFlag);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saOwnerSettleRsvUnSubmitSearch", paramMap);
        OTCSaOwnSettleDTO settleDTO = null;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                settleDTO = new OTCSaOwnSettleDTO();
                settleDTO.setCheck_item("1");
                settleDTO.setOp_team_code(StringUtil.nvl(map.get("op_team_code"), ""));
                settleDTO.setCntr_team_code(StringUtil.nvl(map.get("cntr_team_code"), ""));
                settleDTO.setStl_acc_code(StringUtil.nvl(map.get("stl_cntr_acc_code"), ""));
                settleDTO.setStl_acc_name(StringUtil.nvl(map.get("stl_acc_name"), ""));
                settleDTO.setCurrency_code(StringUtil.nvl(map.get("currency_code"), ""));
                settleDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("entered_amt"), "0.0")));
                settleDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                settleDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("won_amt"), "0.0")));
                settleDTO.setSlip_no(StringUtil.nvl(map.get("slip_no"), ""));
                settleDTO.setGl_acct(StringUtil.nvl(map.get("gl_acct"), ""));
                settleDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("sa_no"), "0.0")));
                settleDTO.setStl_flag(StringUtil.nvl(map.get("stl_flag"), ""));
                // stlFlagCk = settleDTO.getStl_flag();
                settleDTO.setStl_vsl_code(StringUtil.nvl(map.get("stl_vsl_code"), ""));
                settleDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("stl_voy_no"), "0")));
                settleDTO.setUsd_sa_amt(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                settleDTO.setLoc_sa_amt(Double.valueOf(StringUtil.nvl(map.get("loc_sa_amt"), "0.0")));
                settleDTO.setKrw_sa_amt(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                settleDTO.setStl_port_code(StringUtil.nvl(map.get("stl_port_code"), ""));
                settleDTO.setStl_erp_slip_no(StringUtil.nvl(map.get("stl_erp_slip_no"), ""));
                settleDTO.setRemark(StringUtil.nvl(map.get("remark"), ""));
                settleDTO.setCurcy_code(StringUtil.nvl(map.get("curcy_code"), ""));
                settleDTO.setExc_date(Formatter.parseToDate(map.get("exc_date")));
                settleDTO.setExc_rate_type(StringUtil.nvl(map.get("exc_rate_type"), ""));
                settleDTO.setUsd_exc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_exc_rate"), "0.0")));
                settleDTO.setLoc_exc_rate(Double.valueOf(StringUtil.nvl(map.get("loc_exc_rate"), "0.0")));
                settleDTO.setUsd_loc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_loc_rate"), "0.0")));
                settleDTO.setExchange_rate_usd(Double.valueOf(StringUtil.nvl(map.get("usd_loc_rate"), "0.0")));
                settleDTO.setExchange_rate_krw(Double.valueOf(StringUtil.nvl(map.get("loc_exc_rate"), "0.0")));
                //settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs.getTimestamp("exc_date")));
                settleDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("exc_date")));  //RYU
                settleDTO.setDue_date(Formatter.parseToDate(map.get("due_date")));
                settleDTO.setGl_date(Formatter.parseToDate(map.get("gl_date")));
                settleDTO.setPymt_term(StringUtil.nvl(map.get("pymt_term"), ""));
                settleDTO.setTerms_date(Formatter.parseToDate(map.get("terms_date")));
                settleDTO.setPymt_hold_flag(StringUtil.nvl(map.get("pymt_hold_flag"), ""));
                rev.add(settleDTO);
            }
        }
        result.add(rev);
        // **************************** Reserved(Owners' Exp) 가져오기 종료
        // **************************** //
        // **************************** Actual Owners' A/C(Tc/In) 가져오기 시작
        // **************************** //

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", saNo);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saOwnerSettleRsvUnSubmitSearch1", paramMap1);
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap1) {
                settleDTO = new OTCSaOwnSettleDTO();
                settleDTO.setCheck_item("1");
                settleDTO.setOp_team_code(Formatter.nullTrim(String.valueOf(map.get("op_team_code"))));
                settleDTO.setCntr_team_code(Formatter.nullTrim(String.valueOf(map.get("cntr_team_code"))));
                settleDTO.setStl_acc_code(Formatter.nullTrim(String.valueOf(map.get("stl_cntr_acc_code"))));
                settleDTO.setStl_acc_name(Formatter.nullTrim(String.valueOf(map.get("stl_acc_name"))));
                settleDTO.setCurrency_code(Formatter.nullTrim(String.valueOf(map.get("currency_code"))));
                settleDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("entered_amt"), "0.0")));
                settleDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                settleDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("won_amt"), "0.0")));
                settleDTO.setSlip_no(Formatter.nullTrim(String.valueOf(map.get("slip_no"))));
                settleDTO.setGl_acct(Formatter.nullTrim(String.valueOf(map.get("gl_acct"))));
                settleDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("sa_no"), "0.0")));
                settleDTO.setStl_flag(Formatter.nullTrim(String.valueOf(map.get("stl_flag"))));
                // stlFlagCk = settleDTO.getStl_flag();
                settleDTO.setStl_vsl_code(Formatter.nullTrim(String.valueOf(map.get("stl_vsl_code"))));
                settleDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("stl_voy_no"), "0")));
                settleDTO.setUsd_sa_amt(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                settleDTO.setLoc_sa_amt(Double.valueOf(StringUtil.nvl(map.get("loc_sa_amt"), "0.0")));
                settleDTO.setKrw_sa_amt(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                settleDTO.setStl_port_code(Formatter.nullTrim(String.valueOf(map.get("stl_port_code"))));
                settleDTO.setStl_erp_slip_no(Formatter.nullTrim(String.valueOf(map.get("stl_erp_slip_no"))));
                settleDTO.setRemark(Formatter.nullTrim(String.valueOf(map.get("remark"))));
                settleDTO.setCurcy_code(Formatter.nullTrim(String.valueOf(map.get("curcy_code"))));
                settleDTO.setExc_date(Formatter.parseToDate(map.get("exc_date")));
                settleDTO.setExc_rate_type(Formatter.nullTrim(String.valueOf(map.get("exc_rate_type"))));
                settleDTO.setUsd_exc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_exc_rate"), "0.0")));
                settleDTO.setLoc_exc_rate(Double.valueOf(StringUtil.nvl(map.get("loc_exc_rate"), "0.0")));
                settleDTO.setUsd_loc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_loc_rate"), "0.0")));
                settleDTO.setExchange_rate_usd(Double.valueOf(StringUtil.nvl(map.get("usd_loc_rate"), "0.0")));
                settleDTO.setExchange_rate_krw(Double.valueOf(StringUtil.nvl(map.get("loc_exc_rate"), "0.0")));
                //settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(String.valueOf(map.get("exc_date"))));
                settleDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("exc_date")));   //RYU
                settleDTO.setDue_date(Formatter.parseToDate(map.get("due_date")));
                settleDTO.setGl_date(Formatter.parseToDate(map.get("gl_date")));
                settleDTO.setPymt_term(Formatter.nullTrim(String.valueOf(map.get("pymt_term"))));
                settleDTO.setTerms_date(Formatter.parseToDate(map.get("terms_date")));
                settleDTO.setPymt_hold_flag(Formatter.nullTrim(String.valueOf(map.get("pymt_hold_flag"))));
                act.add(settleDTO);
            }
        }
        result.add(act);
        // **************************** Actual Owners' A/C(Tc/In) 가져오기 종료
        // **************************** //
        // **************************** Reserved(Brokerage) 가져오기 시작
        // **************************** //
        StringBuilder sb1 = new StringBuilder();

        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("saNo", saNo);

        List<Map<String, Object>> listMap2 = uxbDAO.select("OTCSADetail.saOwnerSettleRsvUnSubmitSearch2", paramMap2);
        if (listMap2 != null && !listMap2.isEmpty()) {
            for (Map<String, Object> map : listMap2) {
                settleDTO = new OTCSaOwnSettleDTO();
                settleDTO.setCheck_item("1");
                settleDTO.setOp_team_code(StringUtil.nvl(map.get("op_team_code"), ""));
                settleDTO.setCntr_team_code(StringUtil.nvl(map.get("cntr_team_code"), ""));
                settleDTO.setStl_acc_code(StringUtil.nvl(map.get("stl_cntr_acc_code"), ""));
                settleDTO.setStl_acc_name(StringUtil.nvl(map.get("stl_acc_name"), ""));
                settleDTO.setCurrency_code(StringUtil.nvl(map.get("currency_code"), ""));
                settleDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("entered_amt"), "0.0")));
                settleDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                settleDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("won_amt"), "0.0")));
                settleDTO.setSlip_no(StringUtil.nvl(map.get("slip_no"), ""));
                settleDTO.setGl_acct(StringUtil.nvl(map.get("gl_acct"), ""));
                settleDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("sa_no"), "0.0")));
                settleDTO.setStl_flag(StringUtil.nvl(map.get("stl_flag"), ""));
                // stlFlagCk = settleDTO.getStl_flag();
                settleDTO.setStl_vsl_code(StringUtil.nvl(map.get("stl_vsl_code"), ""));
                settleDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("stl_voy_no"), "0")));
                settleDTO.setUsd_sa_amt(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                settleDTO.setLoc_sa_amt(Double.valueOf(StringUtil.nvl(map.get("loc_sa_amt"), "0.0")));
                settleDTO.setKrw_sa_amt(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                settleDTO.setStl_port_code(StringUtil.nvl(map.get("stl_port_code"), ""));
                settleDTO.setStl_erp_slip_no(StringUtil.nvl(map.get("stl_erp_slip_no"), ""));
                settleDTO.setRemark(StringUtil.nvl(map.get("remark"), ""));
                settleDTO.setCurcy_code(StringUtil.nvl(map.get("curcy_code"), ""));
                settleDTO.setExc_date(Formatter.parseToDate(map.get("exc_date")));
                settleDTO.setExc_rate_type(StringUtil.nvl(map.get("exc_rate_type"), ""));
                settleDTO.setUsd_exc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_exc_rate"), "0.0")));
                settleDTO.setLoc_exc_rate(Double.valueOf(StringUtil.nvl(map.get("loc_exc_rate"), "0.0")));
                settleDTO.setUsd_loc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_loc_rate"), "0.0")));
                settleDTO.setExchange_rate_usd(Double.valueOf(StringUtil.nvl(map.get("usd_loc_rate"), "0.0")));
                settleDTO.setExchange_rate_krw(Double.valueOf(StringUtil.nvl(map.get("loc_exc_rate"), "0.0")));
                //settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(String.valueOf(map.get("exc_date"))));
                settleDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("exc_date")));   //RYU
                settleDTO.setDue_date(Formatter.parseToDate(map.get("due_date")));
                settleDTO.setGl_date(Formatter.parseToDate(map.get("gl_date")));
                settleDTO.setPymt_term(StringUtil.nvl(map.get("pymt_term"), ""));
                settleDTO.setTerms_date(Formatter.parseToDate(map.get("terms_date")));
                settleDTO.setPymt_hold_flag(StringUtil.nvl(map.get("pymt_hold_flag"), ""));
                brk.add(settleDTO);
            }
        }
        result.add(brk);
        // **************************** Reserved(Brokerage) 가져오기 종료
        // **************************** //
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Owner Settle역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     */
    public Collection saOwnerSettleApUnSubmitSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo, String stlFlag, String processFlag) {
        Collection result = new ArrayList<>();
        Collection rev = new ArrayList<>();
        Collection act = new ArrayList<>();
        // String stlFlagCk = "";
        // **************************** Account payable / Advance Received
        // 가져오기 시작 **************************** //
        // sb.append(invoice);
        //sb.append(" select  ");
        // sb.append(receipt);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saOwnerSettleApUnSubmitSearch", paramMap);
        OTCSaOwnSettleDTO settleDTO = null;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                settleDTO = new OTCSaOwnSettleDTO();
                settleDTO.setCheck_item("1");
                settleDTO.setOp_team_code(StringUtil.nvl(map.get("op_team_code"), ""));
                settleDTO.setCntr_team_code(StringUtil.nvl(map.get("cntr_team_code"), ""));
                settleDTO.setStl_acc_code(StringUtil.nvl(map.get("stl_cntr_acc_code"), ""));
                settleDTO.setStl_acc_name(StringUtil.nvl(map.get("stl_acc_name"), ""));
                settleDTO.setCurrency_code(StringUtil.nvl(map.get("currency_code"), ""));
                settleDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("entered_amt"), "0.0")));
                settleDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                settleDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("won_amt"), "0.0")));
                settleDTO.setSlip_no(StringUtil.nvl(map.get("slip_no"), ""));
                settleDTO.setGl_acct(StringUtil.nvl(map.get("gl_acct"), ""));
                settleDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("sa_no"), "0.0")));
                settleDTO.setStl_flag(StringUtil.nvl(map.get("stl_flag"), ""));
                // stlFlagCk = settleDTO.getStl_flag();
                settleDTO.setStl_vsl_code(StringUtil.nvl(map.get("stl_vsl_code"), ""));
                settleDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("stl_voy_no"), "0")));
                settleDTO.setUsd_sa_amt(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                settleDTO.setLoc_sa_amt(Double.valueOf(StringUtil.nvl(map.get("loc_sa_amt"), "0.0")));
                settleDTO.setKrw_sa_amt(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                settleDTO.setStl_port_code(StringUtil.nvl(map.get("stl_port_code"), ""));
                settleDTO.setStl_erp_slip_no(StringUtil.nvl(map.get("stl_erp_slip_no"), ""));
                settleDTO.setRemark(StringUtil.nvl(map.get("remark"), ""));
                settleDTO.setCurcy_code(StringUtil.nvl(map.get("curcy_code"), ""));
                settleDTO.setExc_date(Formatter.parseToDate(map.get("exc_date")));
                settleDTO.setExc_rate_type(StringUtil.nvl(map.get("exc_rate_type"), ""));
                settleDTO.setUsd_exc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_exc_rate"), "0.0")));
                settleDTO.setLoc_exc_rate(Double.valueOf(StringUtil.nvl(map.get("loc_exc_rate"), "0.0")));
                settleDTO.setUsd_loc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_loc_rate"), "0.0")));
                settleDTO.setExchange_rate_usd(Double.valueOf(StringUtil.nvl(map.get("usd_loc_rate"), "0.0")));
                settleDTO.setExchange_rate_krw(Double.valueOf(StringUtil.nvl(map.get("loc_exc_rate"), "0.0")));
                //settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs.getTimestamp("exc_date")));
                settleDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("exc_date")));   //RYU
                settleDTO.setDue_date(Formatter.parseToDate(map.get("due_date")));
                settleDTO.setPymt_term(StringUtil.nvl(map.get("pymt_term"), ""));
                settleDTO.setTerms_date(Formatter.parseToDate(map.get("terms_date")));
                settleDTO.setGl_date(Formatter.parseToDate(map.get("gl_date")));
                settleDTO.setPymt_hold_flag(StringUtil.nvl(map.get("pymt_hold_flag"), ""));
                rev.add(settleDTO);
            }
        }
        result.add(rev);
        // **************************** Account payable / Advance Received
        // 가져오기 종료 **************************** //
        // **************************** Account Receivable 가져오기 시작
        // **************************** //

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", saNo);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saOwnerSettleApUnSubmitSearch1", paramMap1);
        if (listMap1 != null && !listMap1.isEmpty()) {
            for (Map<String, Object> map : listMap1) {
                settleDTO = new OTCSaOwnSettleDTO();
                settleDTO.setCheck_item("1");
                settleDTO.setOp_team_code(Formatter.nullTrim(String.valueOf(map.get("op_team_code"))));
                settleDTO.setCntr_team_code(Formatter.nullTrim(String.valueOf(map.get("cntr_team_code"))));
                settleDTO.setStl_acc_code(Formatter.nullTrim(String.valueOf(map.get("stl_cntr_acc_code"))));
                settleDTO.setStl_acc_name(Formatter.nullTrim(String.valueOf(map.get("stl_acc_name"))));
                settleDTO.setCurrency_code(Formatter.nullTrim(String.valueOf(map.get("currency_code"))));
                settleDTO.setEntered_amt(Double.valueOf(StringUtil.nvl(map.get("entered_amt"), "0.0")));
                settleDTO.setUsd_amt(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                settleDTO.setWon_amt(Double.valueOf(StringUtil.nvl(map.get("won_amt"), "0.0")));
                settleDTO.setSlip_no(Formatter.nullTrim(String.valueOf(map.get("slip_no"))));
                settleDTO.setGl_acct(Formatter.nullTrim(String.valueOf(map.get("gl_acct"))));
                settleDTO.setSa_no(Double.valueOf(StringUtil.nvl(map.get("sa_no"), "0.0")));
                settleDTO.setStl_flag(Formatter.nullTrim(String.valueOf(map.get("stl_flag"))));
                // stlFlagCk = settleDTO.getStl_flag();
                settleDTO.setStl_vsl_code(Formatter.nullTrim(String.valueOf(map.get("stl_vsl_code"))));
                settleDTO.setStl_voy_no(Formatter.nullLong(StringUtil.nvl(map.get("stl_voy_no"), "0")));
                settleDTO.setUsd_sa_amt(Double.valueOf(StringUtil.nvl(map.get("usd_sa_amt"), "0.0")));
                settleDTO.setLoc_sa_amt(Double.valueOf(StringUtil.nvl(map.get("loc_sa_amt"), "0.0")));
                settleDTO.setKrw_sa_amt(Double.valueOf(StringUtil.nvl(map.get("krw_sa_amt"), "0.0")));
                settleDTO.setStl_port_code(Formatter.nullTrim(String.valueOf(map.get("stl_port_code"))));
                settleDTO.setStl_erp_slip_no(Formatter.nullTrim(String.valueOf(map.get("stl_erp_slip_no"))));
                settleDTO.setRemark(Formatter.nullTrim(String.valueOf(map.get("remark"))));
                settleDTO.setCurcy_code(Formatter.nullTrim(String.valueOf(map.get("curcy_code"))));
                settleDTO.setExc_date(Formatter.parseToDate(map.get("exc_date")));
                settleDTO.setExc_rate_type(Formatter.nullTrim(String.valueOf(map.get("exc_rate_type"))));
                settleDTO.setUsd_exc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_exc_rate"), "0.0")));
                settleDTO.setLoc_exc_rate(Double.valueOf(StringUtil.nvl(map.get("loc_exc_rate"), "0.0")));
                settleDTO.setUsd_loc_rate(Double.valueOf(StringUtil.nvl(map.get("usd_loc_rate"), "0.0")));
                settleDTO.setExchange_rate_usd(Double.valueOf(StringUtil.nvl(map.get("usd_loc_rate"), "0.0")));
                settleDTO.setExchange_rate_krw(Double.valueOf(StringUtil.nvl(map.get("loc_exc_rate"), "0.0")));
                //settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(String.valueOf(map.get("exc_date"))));
                settleDTO.setExchange_rate_date_krw(Formatter.parseToDate(map.get("exc_date")));  //RYU
                settleDTO.setDue_date(Formatter.parseToDate(map.get("due_date")));
                settleDTO.setPymt_term(Formatter.nullTrim(String.valueOf(map.get("pymt_term"))));
                settleDTO.setTerms_date(Formatter.parseToDate(map.get("terms_date")));
                settleDTO.setGl_date(Formatter.parseToDate(map.get("GL_DATE")));
                settleDTO.setPymt_hold_flag(Formatter.nullTrim(String.valueOf(map.get("pymt_hold_flag"))));
                act.add(settleDTO);
            }
        }
        result.add(act);
        // **************************** Account Receivable 가져오기 종료
        // **************************** //
        return result;
    }

    /**
     * <p>
     * 설명: sa StepNo Search을 요청하는 메소드로 business service call하여 결과값을 리턴 받는다.
     */
    public boolean saSettleExistSearch(Long saNo) {
        boolean result = false;
        // Query 가져오기
        log.debug(">> saNo.longValue() : " + saNo.longValue());

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saSettleExistSearch", paramMap);
        long cnt = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                cnt = StringUtil.toLong((String) map.get("cnt"), 0L);
            }
        }
        if (cnt > 0)
            result = true;
        return result;
    }

    /**
     * <p>
     * 설명: sa brok no를 update한다.
     */
    public String saBrokNoModify(String vslCode, Long voyNo, String chtInOutCd, String costFlag, Long stepNo, String trsactCode, String brokerCode, Long brokNo) throws Exception {
        UserDelegation userInfo = UserInfo.getUserInfo();

        String result = "", chkColumn = "";

        OTCBrokHeadDTO brkDto = new OTCBrokHeadDTO();
			/*if (chtInOutCd.equals("T"))
			brkDto = saDao.brokerageIncomeDetailInquiry(vslCode, voyNo, brokerCode,  );
		} else {*/
        brkDto = saDao.brokerageTCOutDetailInquiry(vslCode, voyNo, brokerCode, chtInOutCd);
        //
        chkColumn = brkDto.getInvoice_check();
			/*			String brok1 = "";
			String brok2 = "";
			String brokChk = "";
			String brokSql = "SELECT BROK_ACC_CODE, BROK_ACC_CODE2 FROM OTC_CP_ITEM_HEAD WHERE  VSL_CODE = '" + Formatter.nullTrim(vslCode) + "'  AND  VOY_NO = " + Formatter.nullLong(voyNo) + "  AND CHT_IN_OUT_CODE = '" + Formatter.nullTrim(chtInOutCd) + "'  ";

			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("vslCode", vslCode);
			paramMap.put("voyNo", voyNo);
			paramMap.put("chtInOutCd", chtInOutCd);
			paramMap.put("costFlag", costFlag);
			paramMap.put("stepNo", stepNo);
			paramMap.put("trsactCode", trsactCode);
			paramMap.put("brokerCode", brokerCode);
			paramMap.put("brokNo", brokNo);

			List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBrokNoModify", paramMap);
			if(listMap != null && !listMap.isEmpty()) {
				for(Map<String, Object> map : listMap) {
					brok1 = String.valueOf(map.get("BROK_ACC_CODE"));
					brok2 = String.valueOf(map.get("BROK_ACC_CODE2"));
				}
			}
			if (Formatter.nullTrim(brok1).equals(Formatter.nullTrim(brokerCode))) {
				brokChk = "1";
			} else if (Formatter.nullTrim(brok2).equals(Formatter.nullTrim(brokerCode))) {
				brokChk = "2";
			}
			*/
        if (!"".equals(vslCode)) {
            // Query 가져오기
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("brokNo", Formatter.nullLong(brokNo));
            paramMap.put("_sessionUserId", Formatter.nullTrim(userInfo.getUserId()));
            paramMap.put("vslCode", Formatter.nullTrim(vslCode));
            paramMap.put("voyNo", Formatter.nullLong(voyNo));
            paramMap.put("chtInOutCd", Formatter.nullTrim(chtInOutCd));
            paramMap.put("stepNo", Formatter.nullLong(stepNo));
            paramMap.put("trsactCode", Formatter.nullTrim(trsactCode));
            paramMap.put("costFlag", Formatter.nullTrim(costFlag));
            paramMap.put("brokerCode", Formatter.nullTrim(brokerCode));
            paramMap.put("chkColumn", Formatter.nullTrim(chkColumn));

            uxbDAO.update("OTCSADetail.saBrokNoModify1", paramMap);
            result = "SUC-0600";
        }
        return result;
    }

    /**
     * <p>
     * 설명: sa brok no를 초기화한다.
     */
    public String saBrokNoCancel(Long brokNo, String brokerCode, String vslCode, Long voyNo, String chtInCode, UserBean userBean) {
        String userId = ObjectUtils.isEmpty(userBean) ? UserInfo.getUserInfo().getUserId() : userBean.getUser_id();

        String result = "", chkColumn = "";
        OTCBrokHeadDTO brkDto = new OTCBrokHeadDTO();
        if (chtInCode.equals("T")) {
            brkDto = saDao.brokerageIncomeDetailInquiry(vslCode, voyNo, brokerCode);
        } else {
            brkDto = saDao.brokerageTCOutDetailInquiry(vslCode, voyNo, brokerCode, chtInCode);
        }
        chkColumn = brkDto.getInvoice_check();
			/*
			String brok1 = "";
			String brok2 = "";
			String brokChk = "";
			String brokSql = "SELECT BROK_ACC_CODE, BROK_ACC_CODE2 FROM OTC_CP_ITEM_HEAD WHERE  VSL_CODE = '" + Formatter.nullTrim(vslCode) + "'  AND  VOY_NO = " + Formatter.nullLong(voyNo) + "  AND CHT_IN_OUT_CODE = '" + Formatter.nullTrim(chtInCode) + "'  ";

			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("brokNo", brokNo);
			paramMap.put("brokerCode", brokerCode);
			paramMap.put("vslCode", vslCode);
			paramMap.put("voyNo", voyNo);
			paramMap.put("chtInCode", chtInCode);

			List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saBrokNoCancel", paramMap);
			if(listMap != null && !listMap.isEmpty()) {
				for(Map<String, Object> map : listMap) {
					brok1 = String.valueOf(map.get("BROK_ACC_CODE"));
					brok2 = String.valueOf(map.get("BROK_ACC_CODE2"));
				}
			}
			if (Formatter.nullTrim(brok1).equals(Formatter.nullTrim(brokerCode))) {
				brokChk = "1";
			} else if (Formatter.nullTrim(brok2).equals(Formatter.nullTrim(brokerCode))) {
				brokChk = "2";
			} */
        StringBuilder sb = new StringBuilder();
        // Query 가져오기
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("_sessionUserId", Formatter.nullTrim(userId));
        paramMap.put("brokNo", Formatter.nullLong(brokNo));
        paramMap.put("chkColumn", Formatter.nullTrim(chkColumn));
        uxbDAO.update("OTCSADetail.saBrokNoCancel1", paramMap);
        log.debug("saBrokNoCancel ryu : " + sb.toString());
        result = "SUC-0600";
        return result;
    }

    /**
     * <p>
     * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 Withholding Tax 정보를 읽어온다
     * chtInOutCOde : O, T, R
     */
    public long saWithholdingCheckSearch(Long saNo, String wthFlag) {
        long result = 0;
        // **************************** Withholding Tax check가져오기 시작
        // **************************** //
        if ("Y".equals(wthFlag)) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("saNo", Formatter.nullLong(saNo));

            List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saWithholdingCheckSearch", paramMap);
            if (listMap != null && !listMap.isEmpty()) {
                for (Map<String, Object> map : listMap) {
                    result = StringUtil.toLong((String) map.get("cnt"), 0L);
                } // rs while
            } // rs while
            // **************************** Withholding Tax check 가져오기 종료
            // **************************** //
        }
        return result;
    }

    /**
     * <p>
     * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 세금계산서를 발행할 정보가 있는지 읽어온다.
     * chtInOutCOde : O, T, R
     */
    public long saVatExistCheckSearch(Long saNo) {
        long result = 0;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", Formatter.nullLong(saNo));

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saVatExistCheckSearch", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                result = StringUtil.toLong((String) map.get("cnt"), 0L);
            } // rs while
        } // rs while
        return result;
    }

    /**
     * <p>
     * 설명: sa 관리화면 ONLOAD시 Search을 요청하는 메소드로 business service call하여 결과값을 리턴
     * 받는다.
     *
     * @return Collection : 이전 스텝의 hire값을 가져온다.
     */
    public Collection saPrestephireSearch(Long saNo) {
        Collection result = null;
        // dao 객체 생성
        if (saNo != null) {
            result = saOnHireSelect(saNo, null, "");  //RYU TO-DO
        }
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     * <p>
     * sa 번호
     *
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     */
    public OTCSaOffHireDTO saOffhireBunkerPriceInitSelect(String vslCode, Long voyNo, String chtInCode, Date fromHire, Date toHire) {
        OTCSaOffHireDTO result = new OTCSaOffHireDTO();
        // **************************** Off Hire 가져오기 시작
        // **************************** //

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", voyNo);
        paramMap.put("chtInCode", chtInCode);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saOffhireBunkerPriceInitSelect", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            result.setDay_hire(Double.parseDouble(StringUtil.nvl(listMap.get(0).get("DAY_HIRE"), "0")));
            result.setAdd_comm(Double.parseDouble(StringUtil.nvl(listMap.get(0).get("ADDR_COMM_RATE"), "0")));
            result.setFo_price(Double.parseDouble(StringUtil.nvl(listMap.get(0).get("FO_PRICE"), "0")));
            result.setDo_price(Double.parseDouble(StringUtil.nvl(listMap.get(0).get("DO_PRICE"), "0")));
            result.setFo_idle(Double.parseDouble(StringUtil.nvl(listMap.get(0).get("FO_IDLE"), "0")));
            result.setDo_idle(Double.parseDouble(StringUtil.nvl(listMap.get(0).get("DO_IDLE"), "0")));
        } // while
        // **************************** Off Hire 가져오기 종료
        // **************************** //
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail Hire내역을 조회하는 메소드이다.
     * <p>
     * sa 번호
     *
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public OTCSaSpeedClaimDTO saOwnerACBunkerInitSearch(String vslCode, Long voyNo, String chtinCd) {
        OTCSaSpeedClaimDTO result = null;
        StringBuilder sb = new StringBuilder();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", voyNo);
        paramMap.put("chtinCd", chtinCd);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saOwnerACBunkerInitSearch", paramMap);
        double add_comm = 0;
        double fo_prc = 0;
        double do_prc = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                add_comm = Formatter.nullDouble(StringUtil.nvl(map.get("ADTNL_COMM_RATE"), "0"));
                fo_prc = Formatter.nullDouble(StringUtil.nvl(map.get("FO_PRICE"), "0"));
                do_prc = Formatter.nullDouble(StringUtil.nvl(map.get("DO_PRICE"), "0"));
            }
        }
        result = new OTCSaSpeedClaimDTO();
        result.setAdd_comm(Double.valueOf(add_comm));
        result.setFo_price(Double.valueOf(fo_prc));
        result.setDo_price(Double.valueOf(do_prc));
        result.setFactor(Double.valueOf(100));
        return result;
    }

    /**
     * <p>
     * 설명: sa 세금계산서 번호를 update한다.
     */
    public String saVatNoModify(Long saNo, Long vatNo, String trsactCd) {
        UserDelegation userInfo = UserInfo.getUserInfo();

        String result = "";
        if (saNo != null) {
            // Query 가져오기

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("saNo", Formatter.nullLong(saNo));
            paramMap.put("vatNo", Formatter.nullLong(vatNo));
            paramMap.put("trsactCd", Formatter.nullTrim(trsactCd));
            paramMap.put("_sessionUserId", userInfo.getUserId());

            uxbDAO.update("OTCSADetail.saVatNoModify", paramMap);
            result = "SUC-0600";
        }
        return result;
    }

    public String saVatNoModify(OTCSaDetailVO saVO) {
        UserDelegation userInfo = UserInfo.getUserInfo();

        String result = "";
        if (saVO.getSa_no() != null) {
            Map<String, Object> paramMap = new HashMap<>();

            // Query 가져오기
            paramMap.put("vat_no", Formatter.nullDouble(StringUtil.nvl(saVO.getVat_no(), "0")));
            paramMap.put("contact_id_1", Formatter.nullDouble(StringUtil.nvl(saVO.getContact_id_1(), "0")));
            paramMap.put("contact_id_2", Formatter.nullDouble(StringUtil.nvl(saVO.getContact_id_2(), "0")));
            paramMap.put("nts_approve_number", Formatter.nullTrim(saVO.getNts_approve_number()));
            paramMap.put("sa_no", Formatter.nullDouble(StringUtil.nvl(saVO.getSa_no(), "0")));
            paramMap.put("trsact_code", Formatter.nullTrim(saVO.getTrsact_code()));
            paramMap.put("tax_code_flag", Formatter.nullTrim(saVO.getTax_code_flag()));
            paramMap.put("group_seq", Formatter.nullDouble(StringUtil.nvl(saVO.getGroup_seq(), "0")));
            paramMap.put("_sessionUserId", userInfo.getUserId());
            uxbDAO.update("OTCSADetail.saVatNoModify1", saVO);
            result = "SUC-0600";
        }
        return result;
    }

    /**
     * <p>
     * 설명:sa 매출 세금계산서 발행 갯수를 조회하는 메소드이다.
     * <p>
     * no
     *
     * @return sa sale vat count
     */
    public Long saSaleVatSelect(Long saNo) {
        Long result = null;
        long cnt = 0;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", Formatter.nullLong(saNo));

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saSaleVatSelect", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            cnt = StringUtil.toLong((String) listMap.get(0).get("cnt"), 0L);
        }
        result = Long.valueOf(cnt);
        return result;
    }

    /**
     * <p>
     * 설명:sa 매입 세금계산서 발행 갯수를 조회하는 메소드이다.
     * <p>
     * no
     *
     * @return sa sale vat count
     */
    public Long saPurchaseVatSelect(Long saNo) {
        Long result = null;
        long cnt = 0;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", Formatter.nullLong(saNo));

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saPurchaseVatSelect", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            cnt = StringUtil.toLong((String) listMap.get(0).get("cnt"), 0L);
        }
        result = Long.valueOf(cnt);
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail 내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public OTCSaDetailDTO saDetailValidate(Long saNo) throws Exception {
        OTCSaDetailDTO result = null;
        StringBuilder sb = new StringBuilder();
        // Query 가져오기
        sb.append("		SELECT count(*) cnt from otc_sa_detail where sa_no = " + saNo.longValue() + " ");
        result = (OTCSaDetailDTO) commonDao.getObject(OTCSaDetailDTO.class, sb.toString());
        return result;
    }

    /**
     * <p>
     * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 Withholding Tax 정보를 읽어온다
     * chtInOutCOde : O, T, R
     */
    public long saWithholdingCheckSearch(Long saNo) {
        long result = 0;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saWithholdingCheckSearch", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                result = StringUtil.toLong((String) map.get("CNT"), 0L);
            } // rs while
        } // rs while
        return result;
    }

    /**
     * <p>
     * 설명:sa AP Detail 내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public OTCSaDetailVO saAPDetailSelect(Long saNo) throws Exception {
        OTCSaDetailVO result = null;
        StringBuilder sb = new StringBuilder();
        // Query 가져오기
        sb.append("		SELECT  V.*   ");
        sb.append("	          FROM OTC_SA_DETAIL V    ");
        sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");
        sb.append(" AND TRSACT_CODE = 'L001' ");
        result = (OTCSaDetailVO) commonDao.getObject(OTCSaDetailVO.class, sb.toString());
        return result;
    }

    /**
     * <p>
     * 설명: 거래처의 미결 금액 정보가 존재한는 항차를 조회한다. chtInOutCOde : O, T, R
     */
    public Collection openVslSearch(String accCode, String vslCode, Long voyNo) {
        Collection result = null;
        SCBVslVoyMDTO voyDTO = null;
        String acc_code = Formatter.nullTrim(accCode);
        String vessel = Formatter.nullTrim(vslCode);
        long voy = Formatter.nullLong(voyNo);
        StringBuilder sb = new StringBuilder();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("accCode", accCode);
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", voyNo);
        paramMap.put("voy", String.valueOf(voy));

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.openVslSearch", paramMap);
        int row = 0;
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                row = row + 1;
                if (row == 1)
                    result = new ArrayList<>();
                voyDTO = new SCBVslVoyMDTO();
                voyDTO.setVsl_code(StringUtil.nvl(map.get("VSL_CODE")));
                voyDTO.setVsl_name(StringUtil.nvl(map.get("VSL_NAME")));
                voyDTO.setVoy_no(Formatter.nullLong(StringUtil.nvl(map.get("VOY_NO"), "0")));
                result.add(voyDTO);
            }
        }
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail 내역을 조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public OTCSaDetailDTO saDetailValidateTax(Long saNo) {
        OTCSaDetailDTO result = new OTCSaDetailDTO();
        long cnt1 = 0;
        long cnt2 = 0;
        StringBuilder sb = new StringBuilder();
        // Query 가져오기
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        // Query 가져오기
        // Query 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saDetailValidateTax", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            result.setCnt(Formatter.nullLong(StringUtil.nvl(listMap.get(0).get("cnt"), "0")));
        }

        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", saNo);

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saDetailValidateTax1", paramMap1);
        if (listMap1 != null && !listMap1.isEmpty()) {
            cnt1 = StringUtil.toLong((String) listMap1.get(0).get("cnt1"), 0L);
        }
        if (cnt1 > 0)
            result.setPur_tax_flag("Y");

        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("saNo", saNo);

        List<Map<String, Object>> listMap2 = uxbDAO.select("OTCSADetail.saDetailValidateTax2", paramMap2);
        if (listMap2 != null && !listMap2.isEmpty()) {
            cnt2 = StringUtil.toLong((String) listMap2.get(0).get("cnt2"), 0L);
        }
        if (cnt2 > 0)
            result.setSale_tax_flag("Y");
        return result;
    }

    /**
     * <p>
     * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 add commition 정보를 읽어온다
     * chtInOutCOde : O, T, R
     */
    public OTCOwnersComDTO saAddCommSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo) {
        OTCOwnersComDTO result = new OTCOwnersComDTO();
        // ADD COMM 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", Formatter.nullLong(saNo));

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saAddCommSearch", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            result.setVat_flag(StringUtil.nvl(listMap.get(0).get("VAT_FLAG"), ""));
            result.setOrg_vat_no(Formatter.nullLong(StringUtil.nvl(listMap.get(0).get("ORG_VAT_NO"), "0")));
            result.setAdd_comm(Formatter.nullDouble(StringUtil.nvl(listMap.get(0).get("SA_RATE"), "0")));
            result.setAdd_comm_usd_amt(Formatter.nullDouble(StringUtil.nvl(listMap.get(0).get("USD_SA_AMT"), "0")));
            result.setAdd_comm_usd_vat_amt(Formatter.nullDouble(StringUtil.nvl(listMap.get(0).get("USD_VAT_SA_AMT"), "0")));
            result.setAdd_comm_krw_amt(Formatter.nullDouble(StringUtil.nvl(listMap.get(0).get("KRW_SA_AMT"), "0")));
            result.setAdd_comm_krw_vat_amt(Formatter.nullDouble(StringUtil.nvl(listMap.get(0).get("KRW_VAT_SA_AMT"), "0")));
            result.setVoyage(Formatter.nullLong(StringUtil.nvl(listMap.get(0).get("VOY_NO"), "0")));     // 채산 항차 추가 ryu 20100203
        }
        //RESERVED
        Map<String, Object> paramMap1 = new HashMap<>();
        paramMap1.put("saNo", Formatter.nullLong(saNo));

        List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saAddCommSearch1", paramMap1);
        if (listMap1 != null && !listMap1.isEmpty()) {
            result.setRsv_usd_amt(Double.valueOf(listMap1.get(0) == null ? "0.0" : StringUtil.nvl(listMap1.get(0).get("USD_SA_AMT"), "0.0")));
        }
        //AP
        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("saNo", Formatter.nullLong(saNo));

        List<Map<String, Object>> listMap2 = uxbDAO.select("OTCSADetail.saAddCommSearch2", paramMap2);
        if (listMap2 != null && !listMap2.isEmpty()) {
            result.setAp_usd_amt(Double.valueOf(StringUtil.nvl(listMap2.get(0).get("USD_SA_AMT"), "0.0")));
        }
        //owner's ac

        Map<String, Object> paramMap3 = new HashMap<>();
        paramMap3.put("saNo", Formatter.nullLong(saNo));

        List<Map<String, Object>> listMap3 = uxbDAO.select("OTCSADetail.saAddCommSearch3", paramMap3);
        if (listMap3 != null && !listMap3.isEmpty()) {
            result.setOwnac_usd_amt(Double.valueOf(StringUtil.nvl(listMap3.get(0).get("USD_SA_AMT"), "0.0")));
        }
        //ar
        Map<String, Object> paramMap4 = new HashMap<>();
        paramMap4.put("saNo", Formatter.nullLong(saNo));

        List<Map<String, Object>> listMap4 = uxbDAO.select("OTCSADetail.saAddCommSearch4", paramMap4);
        if (listMap4 != null && !listMap4.isEmpty()) {
            result.setAr_usd_amt(Double.valueOf(StringUtil.nvl(listMap4.get(0).get("USD_SA_AMT"), "0.0")));
        }
        //total value
        result.setRsv_ttl_usd_amt(Double.valueOf(Formatter.nullDouble(StringUtil.nvl(result.getRsv_usd_amt(), "0")) + Formatter.nullDouble(StringUtil.nvl(result.getAp_usd_amt(), "0"))));
        //total value
        result.setOwnac_ttl_usd_amt(Double.valueOf(Formatter.nullDouble(StringUtil.nvl(result.getOwnac_usd_amt(), "0")) + Formatter.nullDouble(StringUtil.nvl(result.getAr_usd_amt(), "0"))));
        return result;
    }

    /**
     * <p>
     * 설명:sa bunker init내역을 조회하는 메소드이다.
     */
    public boolean saSettleCheck(Long sa_no) {
        boolean result = false;
        int cnt = 0;
        // Query 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sa_no", Formatter.nullLong(sa_no));

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saSettleCheck", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            cnt = Integer.parseInt(String.valueOf(listMap.get(0).get("cnt")));
        }
        if (cnt > 0) result = true;
        return result;
    }

    /**
     * <p>
     * 설명: 용선 원천징수 대상여부 검사
     */
    public String saWithTaxCheck(String vslCode, Long voyNo, String chtInOutCode, Long stepNo) {
        String result = "";
        long saCnt = 0;
        log.debug(" [saWithTaxCheck DAO Start!!]");
        StringBuilder sb = new StringBuilder();
        //sb.append("		and b.TRSACT_CODE in ('A001','A002','G001','G002','H001','H002','H005','I003','I004','I005')	\n");
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        //sb.append("		and b.TRSACT_CODE in ('A001','A002','A006','A007', 'G001','G002','G003','G004', 'H001','H002','H009','H010', 'H005','H011', 'I003','I004','I005','I071','I072','I073')	\n");
        //위 REMARK 230309 GYJ
        //SPD CLAIM ACTUAL 생성+원천징수 생성 이후 SPD CLAIM RESERVE로 변경하게 되면 기존 원천징수 인식하지 못하여 화면에서 삭제 여부 묻지 않아 ACTUAL당시 BASE 금액이 그대로 신고되는 문제 발생.
        //하여 재경 이경화 협의하여 무엇을 저장하든 원천징수 자료가 해당 전표번호로 생성되어 있는 경우 자동으로 삭제할 수 있도록 함.
        log.debug("vslCode:" + vslCode);
        log.debug("voyNo:" + voyNo.toString());
        log.debug("chtInOutCode:" + chtInOutCode);
        log.debug("stepNo:" + stepNo.toString());
        log.debug(" [saWithTaxCheck query]" + sb.toString());
        int i = 1;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", voyNo);
        paramMap.put("chtInOutCode", chtInOutCode);
        paramMap.put("stepNo", stepNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saWithTaxCheck", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                saCnt = StringUtil.toLong((String) map.get("saCnt"), 0L);
            }
        }
        if (saCnt > 0) {
            result = "Y";
        } else {
            result = "N";
        }
        log.debug("[saWithTaxCheck DAO result]:" + result);
        return result;
    }

    /**
     * <p>
     * 설명: 용선 원천징수 저장
     */
    public String saWithTaxInvoiceInsert(String vslCode, Long voyNo, String chtInOutCode, Long stepNo,
                                         OTCSaWithholdingTaxDTO otcSaWithInfo) throws Exception {

        UserDelegation userInfo = UserInfo.getUserInfo();

        String result = "", vslOwnCode = "", cht_in_out_code = "", gl_acc_code = "", evidence_flag = "";
        StringBuilder sb = new StringBuilder();
        // sa seq max 값 가져오기

        //conn.setAutoCommit(false);

        vslOwnCode = Formatter.nullTrim(vslDao.vesselOwnSelect(otcSaWithInfo.getVsl_code()));
        if (vslOwnCode.equals("L")) {
            if ("T".equals(otcSaWithInfo.getCht_in_out_code())) {
                cht_in_out_code = "C";
            } else {
                cht_in_out_code = "L";
            }
        } else {
            cht_in_out_code = otcSaWithInfo.getCht_in_out_code();
        }
        if ("KR".equals(otcSaWithInfo.getNat_code())) {
            evidence_flag = "09";
        } else {
            evidence_flag = "06";
        }
        // 화폐 USD 고정
        // PYMT_METH
        //trsact_code : M001, income tax 소득세 = 지급총액/(1-rate*.1.1)

        Map<String, Object> paramMap = new HashMap<>();

        // [1]
        paramMap.put("with_sa_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getWith_sa_no(), "0")));
        paramMap.put("max_sa_no", Formatter.nullLong(saSeqMaxNoSelect(otcSaWithInfo.getWith_sa_no())));
        paramMap.put("vsl_code", Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
        paramMap.put("voy_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getVoy_no(), "0")));
        // [2]
        paramMap.put("income_tax_amt_usd", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getIncome_tax_amt_usd(), "0")));
        paramMap.put("income_tax_amt_usd", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getIncome_tax_amt_usd(), "0")));
        paramMap.put("income_tax_amt_krw", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getIncome_tax_amt_krw(), "0")));
        // [3]
        paramMap.put("curcy_code", Formatter.nullTrim(otcSaWithInfo.getCurcy_code()));
        paramMap.put("exc_rate_type", Formatter.nullTrim(otcSaWithInfo.getExc_rate_type()));
        // [4]
        paramMap.put("pymt_hold_flag", Formatter.nullTrim(otcSaWithInfo.getPymt_hold_flag()));
        //ps.setString(i++, Formatter.nullTrim(otcSaWithInfo.getPymt_meth()));
        // [5]
        paramMap.put("income_tax_base_usd", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getIncome_tax_base_usd(), "0")));   // taxable income
        paramMap.put("income_tax_rate", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getIncome_tax_rate(), "0")));       // tax rate
        System.out.println("RYU 원천징수 확인 otcSaWithInfo.getIncome_tax_rate()=" + otcSaWithInfo.getIncome_tax_rate());
        // [6]
        // [7]
        // [8]
        // [9]
        paramMap.put("vsl_code", Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
        paramMap.put("voy_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getVoy_no(), "0")));
        paramMap.put("cntr_acc_code", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getCntr_acc_code(), "0")));
        // [10]
        paramMap.put("usd_exc_rate", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getUsd_exc_rate(), "0")));
        paramMap.put("loc_exc_rate", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getLoc_exc_rate(), "0")));
        paramMap.put("usd_loc_rate", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getUsd_loc_rate(), "0")));
        // [11]
        paramMap.put("bank_acc_id", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getBank_acc_id(), "0")));
        paramMap.put("bank_acc_desc", Formatter.nullTrim(otcSaWithInfo.getBank_acc_desc()));
        // [12]
        CCDTrsactTypeMVO tVO = tdao.ccdTrsactTypeMPaymentSelect("SOMO", cht_in_out_code, "M001");
        if (tVO != null) {
            gl_acc_code = Formatter.nullTrim(tVO.getO_gl_acc_code());   //RYU 2010.09.29
        }
        paramMap.put("gl_acc_code", gl_acc_code);
        paramMap.put("evidence_flag", evidence_flag);
        // [13]
        uxbDAO.insert("OTCSADetail.saWithTaxInvoiceInsert", paramMap);

        Map<String, Object> paramMap1 = new HashMap<>();
        // trsact_code : M002, inhabitants tax 주민세 = (지급총액/(1-rate*.1.1))*0.1
        // [1]
        paramMap1.put("with_sa_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getWith_sa_no(), "0")));
        paramMap1.put("max_sa_no", Formatter.nullLong(saSeqMaxNoSelect(otcSaWithInfo.getWith_sa_no())));
        paramMap1.put("vsl_code", Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
        paramMap1.put("voy_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getVoy_no(), "0")));
        // [2]
        paramMap1.put("inhabit_tax_amt_usd", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getInhabit_tax_amt_usd(), "0")));
        paramMap1.put("inhabit_tax_amt_usd", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getInhabit_tax_amt_usd(), "0")));
        paramMap1.put("inhabit_tax_amt_krw", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getInhabit_tax_amt_krw(), "0")));
        // [3]
        paramMap1.put("curcy_code", Formatter.nullTrim(otcSaWithInfo.getCurcy_code()));
        paramMap1.put("exc_rate_type", Formatter.nullTrim(otcSaWithInfo.getExc_rate_type()));
        // [4]
        paramMap1.put("pymt_hold_flag", Formatter.nullTrim(otcSaWithInfo.getPymt_hold_flag()));
        //ps.setString(r++, Formatter.nullTrim(otcSaWithInfo.getPymt_meth()));
        // [5]
        paramMap1.put("inhabit_tax_base_usd", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getInhabit_tax_base_usd(), "0")));  // taxable inhabit
        paramMap1.put("inhabit_tax_rate", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getInhabit_tax_rate(), "0")));      // tax rate
        System.out.println("RYU 원천징수 확인 otcSaWithInfo.getInhabit_tax_rate()=" + otcSaWithInfo.getInhabit_tax_rate());
        // [6]
        // [7]
        // [8]
        // [9]
        paramMap1.put("vsl_code", Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
        paramMap1.put("voy_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getVoy_no(), "0")));
        paramMap1.put("cntr_acc_code", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getCntr_acc_code(), "0")));
        // [10]
        paramMap1.put("usd_exc_rate", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getUsd_exc_rate(), "0")));
        paramMap1.put("loc_exc_rate", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getLoc_exc_rate(), "0")));
        paramMap1.put("usd_loc_rate", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getUsd_loc_rate(), "0")));
        // [11]
        paramMap1.put("bank_acc_id", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getBank_acc_id(), "0")));
        paramMap1.put("bank_acc_desc", Formatter.nullTrim(otcSaWithInfo.getBank_acc_desc()));
        // [12]
        tVO = tdao.ccdTrsactTypeMPaymentSelect("SOMO", cht_in_out_code, "M002");
        if (tVO != null) {
            gl_acc_code = Formatter.nullTrim(tVO.getO_gl_acc_code());   //RYU 2010.09.29
        }

        paramMap1.put("gl_acc_code", gl_acc_code);
        paramMap1.put("evidence_flag", evidence_flag);
        // [13]
        uxbDAO.insert("OTCSADetail.saWithTaxInvoiceInsert", paramMap1);
        //---------------------------- 형일 수정 (시작) --------------------------------------------
        log.debug("getWth_tax_calc_method  : " + otcSaWithInfo.getWth_tax_calc_method());
        //------------------------------------------------------------------------
        // ## 원천징수 보완 ## ( hijang - 2012.04.19 )
        // Gross-Up 방식  :  M004 가 발생해야 한다. ( 원래 방식 그대로 )
        // Deduction 방식  : M004 가 발생되면 안된다..!!
        //------------------------------------------------------------------------
        if (otcSaWithInfo.getWth_tax_calc_method().equals("1")) {    // Gross-Up 방식
            // trsact_code : M004, onhire(withholding tax) 지급운임기타 = 소득세+주민세[ 지급총액/(1-rate*.1.1) + (지급총액/(1-rate*.1.1))*0.1 ]
            // [1]
            Map<String, Object> paramMap2 = new HashMap<>();

            paramMap2.put("with_sa_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getWith_sa_no(), "0")));
            paramMap2.put("max_sa_no", Formatter.nullLong(saSeqMaxNoSelect(otcSaWithInfo.getWith_sa_no())));
            paramMap2.put("vsl_code", Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
            paramMap2.put("voy_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getVoy_no(), "0")));
            // [2]
            paramMap2.put("income_tax_amt_usd", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getIncome_tax_amt_usd(), "0")) + Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getInhabit_tax_amt_usd(), "0")));
            paramMap2.put("income_tax_amt_usd", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getIncome_tax_amt_usd(), "0")) + Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getInhabit_tax_amt_usd(), "0")));
            paramMap2.put("income_tax_amt_krw", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getIncome_tax_amt_krw(), "0")) + Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getInhabit_tax_amt_krw(), "0")));
            // [3]
            paramMap2.put("curcy_code", Formatter.nullTrim(otcSaWithInfo.getCurcy_code()));
            paramMap2.put("exc_rate_type", Formatter.nullTrim(otcSaWithInfo.getExc_rate_type()));
            // [4]
            paramMap2.put("pymt_hold_flag", Formatter.nullTrim(otcSaWithInfo.getPymt_hold_flag()));
            //ps.setString(s++, Formatter.nullTrim(otcSaWithInfo.getPymt_meth()));
            // [5]
            // [6]
            // [7]
            // [8]
            // [9]
            paramMap2.put("vsl_code", Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
            paramMap2.put("voy_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getVoy_no(), "0")));
            paramMap2.put("cntr_acc_code", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getCntr_acc_code(), "0")));
            // [10]
            paramMap2.put("usd_exc_rate", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getUsd_exc_rate(), "0")));
            paramMap2.put("loc_exc_rate", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getLoc_exc_rate(), "0")));
            paramMap2.put("usd_loc_rate", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getUsd_loc_rate(), "0")));
            // [11]
            paramMap2.put("bank_acc_id", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getBank_acc_id(), "0")));
            paramMap2.put("bank_acc_desc", Formatter.nullTrim(otcSaWithInfo.getBank_acc_desc()));
            // [12]
            tVO = tdao.ccdTrsactTypeMPaymentSelect("SOMO", cht_in_out_code, "M004");
            if (tVO != null) {
                gl_acc_code = Formatter.nullTrim(tVO.getO_gl_acc_code());   //RYU 2010.09.29
            }
            paramMap2.put("gl_acc_code", gl_acc_code);
            paramMap2.put("evidence_flag", evidence_flag);
            // [13]
            uxbDAO.insert("OTCSADetail.saWithTaxInvoiceInsert", paramMap2);
        } else {
            //----------------------------------------------------------------------
            // Deduction 방식은 'M004' 가 생성 안되는 관계로, balance 재계산처리(hijang)
            //----------------------------------------------------------------------

            double cr_loc_amt = 0;
            double cr_won_amt = 0;
            double dr_loc_amt = 0;
            double dr_won_amt = 0;
            double loc_amt = 0;
            double won_amt = 0;
            // debit balance 총합을 구함 ============================================
            otcSaWithInfo.setWith_sa_no(Formatter.nullLong(StringUtil.nvl(otcSaWithInfo.getWith_sa_no(), "0")));
            Map<String, Object> paramMap2 = new HashMap<>();
            paramMap2.put("with_sa_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getWith_sa_no(), "0")));
            List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saWithTaxInvoiceInsert3", paramMap2);
            if (listMap != null && !listMap.isEmpty()) {
                for (Map<String, Object> map : listMap) {
                    dr_loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                    dr_won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_amt"), "0.0")));
                }
            }
            // credit 총합을 구함 ============================================
            Map<String, Object> paramMap3 = new HashMap<>();
            paramMap3.put("with_sa_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getWith_sa_no(), "0")));

            List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.saWithTaxInvoiceInsert4", paramMap3);
            if (listMap1 != null && !listMap1.isEmpty()) {
                for (Map<String, Object> map : listMap1) {
                    cr_loc_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("usd_amt"), "0.0")));
                    cr_won_amt = Formatter.nullDouble(Double.valueOf(StringUtil.nvl(map.get("krw_amt"), "0.0")));
                }
            }
            loc_amt = dr_loc_amt - cr_loc_amt;
            won_amt = dr_won_amt - cr_won_amt;

            Map<String, Object> paramMap4 = new HashMap<>();
            paramMap4.put("loc_amt", loc_amt);
            paramMap4.put("loc_amt", loc_amt);
            paramMap4.put("won_amt", won_amt);
            paramMap4.put("with_sa_no", Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getWith_sa_no(), "0")));
            paramMap4.put("trsact_code", (loc_amt >= 0) ? "L001" : "L002");
            uxbDAO.update("OTCSADetail.saWithTaxInvoiceInsert5", paramMap4);
        }
        //---------------------------- 형일 수정 (종료) --------------------------------------------
        // 초기화
        sb.setLength(0);
        // Query 가져오기
        otcSaWithInfo.setOnHire_balance(Formatter.nullDouble(StringUtil.nvl(otcSaWithInfo.getOnHire_balance(), "0")));
        otcSaWithInfo.setWith_sa_no(Formatter.nullLong(StringUtil.nvl(otcSaWithInfo.getWith_sa_no(), "0")));
        otcSaWithInfo.setVsl_code(Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
        otcSaWithInfo.setVoy_no(Formatter.nullLong(StringUtil.nvl(otcSaWithInfo.getVoy_no(), "0")));
        otcSaWithInfo.setStep_no(Formatter.nullLong(StringUtil.nvl(otcSaWithInfo.getStep_no(), "0")));
        uxbDAO.update("OTCSADetail.saWithTaxInvoiceInsert6", otcSaWithInfo);
        //conn.commit();
        //conn.setAutoCommit(true);
        result = "SUC-0100";
        return result;
    }

    public String saWithTaxInvoiceDelete(Long saNo, String vslCode, Long voyNo, String chtInOutCode, Long stepNo, OTCSaHeadDTO otcSaHeadDto) {
        String result = "";
        // Query 가져오기
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);
        paramMap.put("vslCode", vslCode);
        paramMap.put("voyNo", voyNo);
        uxbDAO.delete("OTCSADetail.saWithTaxInvoiceDelete", paramMap);
        // 초기화
        // Query 가져오기
        otcSaHeadDto.setSa_no(Formatter.nullLong(StringUtil.nvl(otcSaHeadDto.getSa_no(), "0")));
        otcSaHeadDto.setVsl_code(Formatter.nullTrim(otcSaHeadDto.getVsl_code()));
        otcSaHeadDto.setVoy_no(Formatter.nullLong(StringUtil.nvl(otcSaHeadDto.getVoy_no(), "0")));
        otcSaHeadDto.setStep_no(Formatter.nullLong(StringUtil.nvl(otcSaHeadDto.getStep_no(), "0")));
        uxbDAO.update("OTCSADetail.saWithTaxInvoiceDelete1", otcSaHeadDto);
        result = "delete";
        return result;
    }

    public String saHeadWthHireBalReuceRateUpdate(OTCSaHeadVO saHeadVO) {
        String result = "";
        long saCnt = 0;
        log.debug("sa_no:" + saHeadVO.getSa_no());
        //sb.append("		and b.trsact_code in ('A001','A002','G001','G002','H001','H002','H005','I003','I004','I005')	\n");
        //결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
        saHeadVO.setSa_no(saHeadVO.getSa_no().longValue());
        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.saHeadWthHireBalReuceRateUpdate", saHeadVO);
        for (Map<String, Object> map : listMap) {
            saCnt = StringUtil.toLong((String) map.get("saCnt"), 0L);
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sa_no", saHeadVO.getSa_no().longValue());
        paramMap.put("vsl_code", saHeadVO.getVsl_code());
        paramMap.put("voy_no", saHeadVO.getVoy_no().longValue());
        paramMap.put("step_no", saHeadVO.getStep_no().longValue());
        paramMap.put("cht_in_out_code", saHeadVO.getCht_in_out_code());

        // 초기화
        if (saCnt > 0) {
            // Query 가져오기
            paramMap.put("wth_hire_bal_reduce_rate", 1);
            result = "Y";
        } else {
            // Query 가져오기
            int q = 1;
            paramMap.put("wth_hire_bal_reduce_rate", 0);
            result = "N";
        }
        uxbDAO.update("OTCSADetail.saHeadWthHireBalReuceRateUpdate1", paramMap);
        return result;
    }

    /**
     * <p>
     * 설명:sa Detail 중 pymt hold flag 를  조회하는 메소드이다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     * @author hermosa 111018
     */
    public OTCSaDetailVO saDetailCheckPymtHoldFlag(Long saNo) throws Exception {
        OTCSaDetailVO result = null;
        StringBuilder sb = new StringBuilder();
        // Query 가져오기
        sb.append("		SELECT V.PYMT_HOLD_FLAG   ");
        sb.append("	          FROM OTC_SA_DETAIL V    ");
        sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");
        sb.append(" AND PYMT_HOLD_FLAG = 'Y' ");
        sb.append(" AND TRSACT_CODE = 'L001' ");
        result = (OTCSaDetailVO) commonDao.getObject(OTCSaDetailVO.class, sb.toString());
        return result;
    }

    /*
     * 법원허가번호 update 한다.
     * AP가 존재할 경우에 AP LINE 에 저장한다.
     */
    public String courtAdmitNoUpdate(OTCBalanceHeadDTO infos) {
        UserDelegation userInfo = UserInfo.getUserInfo();

        String result = "";
        if (infos.getSa_no() != null) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("sa_no", infos.getSa_no());
            paramMap.put("court_flag", Formatter.nullTrim(infos.getCourt_flag()));
            paramMap.put("court_admit_no", Formatter.nullTrim(infos.getCourt_admit_no()));
            paramMap.put("_sessionUserId", userInfo.getUserId());
            uxbDAO.update("OTCSADetail.courtAdmitNoUpdate", paramMap);
            result = "SUC-0600";
        }
        return result;
    }

    /*
     * SOA 입력 ITEM 들의 FROM-TO 기간에 대해서, 해당 채산항차 START-END DATE 에 걸치지 않은건 있는지 체크한다.
     * 2014.12.23  HIJANG
     */
    public ArrayList SoaVsCBDurationCheck(String vslCode, Long VoyNo, String chtInOut, Long stepNo) {
        ArrayList array = null;
        String invalid_item = "";
        StringBuilder sb = new StringBuilder();
			/*---------------------------------------------------------------------------------------------------------------------------------------
			// 테스트 데이터
			//sb.append("\n             CB_ALL_VOY_INFO_V CB                                ");
			---------------------------------------------------------------------------------------------------------------------------------------*/
        // GMT 와 LOCAL 시간 - DIFF 차이 처리용 ( hijang 2015.02.09 )
        //----------------------------------------------------------------------------------------------------
        // SOA와 채산상의 시간 DIFF(GMT 와 LOCAL 시간) 로 인하여,
        // 기존 채산 VOY_STRT_DATE 와 VOY_END_DATE 를 +/- 12H 만큼씩 보정해줌 !! ( HIJANG 2015.02.09 )
        //---------------------------------------------------------------------------------------------------
        //SOA 기간(FROM-TO) 이 채산항차 기간( VOY_STRT_DATE,VOY_END_DATE) 에 포함된 갯수 체크용 ( hijang 2015.02.10 )
        log.debug(" SoaVsCBDurationCheck : \n" + sb.toString());
        int i = 1;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("VoyNo", VoyNo);
        paramMap.put("chtInOut", chtInOut);
        paramMap.put("stepNo", stepNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.soaVsCBDurationCheck", paramMap);
        array = new ArrayList<>();
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                invalid_item = String.valueOf(map.get("TRSACT_NAME"));
                //log.debug("invalid_item : "+ invalid_item) ;
                array.add(invalid_item);
            }
        }
        return array;
    }

    /**
     * <p>
     * 설명:sa Detail 내역을 조회하는 메소드이다.
     * SOA, CB의 항차를 비교하여 모항차가 일치하지 않는 경우 배분하여 I/F WRITE한다.
     *
     * @param saNo :
     *             sa 번호
     * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
     * saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
     */
    public Collection SoaCBDiffAmtUpdateProdedureCall(Long saNo, String vslCode, Long VoyNo, String chtInOut, Long stepNo) throws Exception {
        UserDelegation userInfo = UserInfo.getUserInfo();

        Collection result = null;
        StringBuilder sb = new StringBuilder();
        ArrayList inVariable = new ArrayList<>();
        inVariable.add(String.valueOf(vslCode));
        inVariable.add(VoyNo);
        inVariable.add(String.valueOf(chtInOut));
        inVariable.add(stepNo);
        inVariable.add(String.valueOf(saNo));
        inVariable.add(userInfo.getUserId());
        log.debug(">>>>>>>P_SET_SA_CB_DIFF_AMT_UPDATE :---start");
        log.debug(">>>>>>>inVar.size()    : " + inVariable.size());

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);
        paramMap.put("vslCode", vslCode);
        paramMap.put("VoyNo", VoyNo);
        paramMap.put("chtInOut", chtInOut);
        paramMap.put("stepNo", stepNo);
        paramMap.put("_sessionUserId", userInfo.getUserId());

        //List<LinkedHashMap<String, Object>> listMap = uxbDAO.select("OTCSADetail.soaCBDiffAmtUpdateProdedureCall", paramMap);
        //Object objs[]       = commonDao.getObjectCstmt(conn,sb.toString(),inVariable.toArray(),1);

        sb.append("{ call P_SET_SA_CB_DIFF_AMT_UPDATE(?,?,?,?,?,?,?,?)  }");
        Object[] listMap = commonDao.getObjectCstmt(sb.toString(), inVariable.toArray(), 2);

        log.debug(">>>>>>>P_SET_SA_CB_DIFF_AMT_UPDATE :---end");
        int row = 0;
        String msg = "";
        String msgcode = "";
        if (listMap.length > 0) {
            LinkedHashMap map = (LinkedHashMap) listMap[0];
            List<String> keyList = new ArrayList<>(map.keySet());
            msg = String.valueOf(map.get(keyList.get(row++)));
            msgcode = String.valueOf(map.get(keyList.get(row++)));
        }
        log.debug(">>>>>>msg code :" + msgcode);
        log.debug(">>>>>>msg :" + msg);
        if ("0".equals(Formatter.nullTrim(msgcode))) {
            // 성공
            log.debug(">>>>>>P_SET_SA_CB_DIFF_AMT_UPDATE   작업  정상적으로 테이블 생성되었습니다. ");
            log.debug(">>>>>>otc_sa_cb_detail  TABLE INQUIRY  ");
            //soa temp table 호출
            sb.append("			select a.*,     																	\n");
            sb.append("                trsact_name_func('SOMO',b.cht_in_out_code,a.trsact_code) as trsact_name,  	\n");
            sb.append("				   b.posting_date, b.cht_in_out_code, b.op_team_code 							\n");
            sb.append("            from otc_sa_cb_detail a, otc_sa_head b where a.sa_no = b.sa_no  					\n");
            sb.append(" 			AND A.SA_NO = " + saNo.longValue() + " ");
            //CVE 0금액은 빼고 IF 로 넘긴다 170329 GYJ
            sb.append("             AND (TRSACT_CODE, LOC_SA_AMT) NOT IN (SELECT 'A003',0 FROM DUAL)                \n");
            //EAR_IF_LINES_ALL 에 I/F 시, ON-HIRE ADD.COMM 에 대해서.. 'ON-HIRE' 의 세금계산서 번호를 가져와서 VAT_CREATION_NO 컬럼에 못넣어주는 오류 수정함 (HIJANG 20171031)
            sb.append("             ORDER BY A.SA_NO, A.ORI_SA_SEQ               \n");
            result = commonDao.getObjects(OTCSaCbDetailDTO.class, sb.toString());
        } else {
            log.error(">>>>>>P_SET_SA_CB_DIFF_AMT_UPDATE 작업 시 오류 발생했습니다. ");
            // Error 메시지
            throw new UxbBizException("[System Error] P_SET_SA_CB_DIFF_AMT_UPDATE 작업 도중 오류가 발생했습니다.\n시스템 담당자에게 문의 바랍니다.");
        }
        return result;
    }

    /*
     * CB에 의해 배분된 금액과 순수 SOA금액을 최종적으로 확인한다. 100% 일치해야함.
     */
    public String SoaVsCBAmtCheck(String saNo) {
        String result = null;
        double locDiffAmt = 0;
        double usdDiffAmt = 0;
        long krwDiffAmt = 0;
        double saDurRate = 0;
        StringBuilder sb = new StringBuilder();
        //sb.append("		  AND A.TRSACT_CODE IN ( 'A006', 'A007', 'A008', 'A009', 'H009', 'H010'				\n");
        log.debug(" SoaVsCBAmtCheck : \n" + sb.toString());
        int i = 1;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.soaVsCBAmtCheck", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                saDurRate = Formatter.nullDouble(StringUtil.nvl(map.get("DIFF_RATE_DUR"), "0"));
                locDiffAmt = Formatter.nullDouble(StringUtil.nvl(map.get("LOC_DIFF_AMT"), "0"));
                usdDiffAmt = Formatter.nullDouble(StringUtil.nvl(map.get("USD_DIFF_AMT"), "0"));
                krwDiffAmt = StringUtil.toLong((String) map.get("KRW_DIFF_AMT"), 0L);
                log.debug("saDurRate : " + saDurRate);
                log.debug("locDiffAmt : " + locDiffAmt);
                log.debug("usdDiffAmt : " + usdDiffAmt);
                log.debug("krwDiffAmt : " + krwDiffAmt);
                if (saDurRate != 0 || locDiffAmt != 0 || usdDiffAmt != 0 || krwDiffAmt != 0) {
                    if (saDurRate != 0) {
                        result = "DUR";
                    }
                    if (locDiffAmt != 0) {
                        result = "LOC AMT";
                    }
                    if (usdDiffAmt != 0) {
                        result = "USD AMT";
                    }
                    if (krwDiffAmt != 0) {
                        result = "KRW AMT";
                    }
                } else {
                    result = "";
                }
            }
        }
        return result;
    }

    /*
     * CB와 SOA의 Contract과 vsl이 동일한지 확인.
     */
    public String SoaVsCBContractVslCheck(String vslCode, String contractNo) {
        String result = null;
        int cnt = 0;
        StringBuilder sb = new StringBuilder();
        log.debug(" vslCode : " + vslCode);
        log.debug(" contractNo : " + contractNo);
        log.debug(" SoaVsCBContractCheck : \n" + sb.toString());
        int i = 1;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("contractNo", contractNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.soaVsCBContractVslCheck", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                cnt = StringUtil.toInt(map.get("CNT"), 0);
                log.debug("cnt : " + cnt);
                if (cnt == 0) {
                    result = " 채산과 SOA의 계약번호 또는 Vessel Code가 상이 합니다.\n 확인바랍니다.\n";
                } else {
                    result = "";
                }
            }
        }
        return result;
    }

    /*
     * CB에 의해 배분된 금액과 순수 SOA금액을 최종적으로 확인한다. 100% 일치해야함.
     */
    public void soaCbDetailDivide(String start_date, String end_date) {
        //sb.append("		AND nvl(A.PROCESS_STS_FLAG,'N') = 'Y'								\n");
        log.debug(" start_date : " + start_date);
        log.debug(" end_date : " + end_date);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("start_date", start_date);
        paramMap.put("end_date", end_date);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.soaCbDetailDivide", paramMap);
        String vslCode = "";
        Long voyNo = 0L;
        String chtInCd = "";
        Long stepNo = 0L;
        String cntrNo = "";
        Long saNo = 0L;
        String ifType = "I";
        Timestamp cancelPostDate = null;
        String cancelReason = "";
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                vslCode = String.valueOf(map.get("VSL_CODE"));
                voyNo = StringUtil.toLong((String) map.get("VOY_NO"), 0L);
                chtInCd = String.valueOf(map.get("CHT_IN_OUT_CODE"));
                stepNo = StringUtil.toLong((String) map.get("STEP_NO"), 0L);
                cntrNo = String.valueOf(map.get("CNTR_NO"));
                saNo = StringUtil.toLong((String) map.get("SA_NO"), 0L);
                log.debug("-------------------------------------");
                log.debug("vsl_Code : " + vslCode);
                log.debug("voy_No : " + voyNo);
                log.debug("cht+In_Cd : " + chtInCd);
                log.debug("step_No : " + stepNo);
                log.debug("cntr_No : " + cntrNo);
                log.debug("SA_NO : " + saNo);
                log.debug("-------------------------------------");
                // 2) 항차매핑 및 배분 작업
                this.soaCbDetailDivideCreate(vslCode, voyNo, chtInCd, stepNo, cntrNo, saNo, ifType, cancelPostDate, cancelReason);
            }
        }
        //return result;
    }

    /**
     * <p>
     * 설명: 로컬에서 SOA 채산항차매핑 및 배분작업 후, 자료 생성 및 추출하기 위해 사용한다.
     * ( OTC_SA_CB_DETAIL_UPLOAD 테이블 )
     */
    public Collection SoaCBDiffAmtCreateProdedureCall(Long saNo, String vslCode, Long VoyNo, String chtInOut, Long stepNo) throws Exception {
        UserDelegation userInfo = UserInfo.getUserInfo();

        Collection result = null;
        StringBuilder sb = new StringBuilder();

        log.debug(">>>>>>>P_SET_SA_CB_DIFF_AMT_CREATE :---start");
        // P_SET_SA_CB_DIFF_AMT_CREATE 프로시져 호출

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);
        paramMap.put("vslCode", vslCode);
        paramMap.put("VoyNo", VoyNo);
        paramMap.put("chtInOut", chtInOut);
        paramMap.put("stepNo", stepNo);

        //List<LinkedHashMap<String, Object>> listMap = uxbDAO.select("OTCSADetail.soaCBDiffAmtCreateProdedureCall", paramMap);
        //Object objs[]       = commonDao.getObjectCstmt(conn,sb.toString(),inVariable.toArray(),1);

        ArrayList inVariable = new ArrayList<>();
        inVariable.add(String.valueOf(vslCode));
        inVariable.add(VoyNo);
        inVariable.add(String.valueOf(chtInOut));
        inVariable.add(stepNo);
        inVariable.add(String.valueOf(saNo));
        inVariable.add(userInfo.getUserId());
        sb.append("{ call P_SET_SA_CB_DIFF_AMT_CREATE(?,?,?,?,?,?,?,?)  }");
        Object[] listMap = commonDao.getObjectCstmt(sb.toString(), inVariable.toArray(), 2);

        log.debug(">>>>>>>P_SET_SA_CB_DIFF_AMT_CREATE :---end");
        int row = 0;
        String msg = "";
        String msgcode = "";
        if (listMap.length > 0) {
            LinkedHashMap map = (LinkedHashMap) listMap[0];
            List<String> keyList = new ArrayList<>(map.keySet());
            msg = String.valueOf(map.get(keyList.get(row++)));
            msgcode = String.valueOf(map.get(keyList.get(row++)));
        }

        log.debug(">>>>>>msg code :" + msgcode);
        log.debug(">>>>>>msg :" + msg);
        if ("0".equals(Formatter.nullTrim(msgcode))) {
            // 성공
            log.debug(">>>>>>P_SET_SA_CB_DIFF_AMT_CREATE   작업  정상적으로 테이블 생성되었습니다. ");
            log.debug(">>>>>>otc_sa_cb_detail  TABLE INQUIRY  ");
            //soa temp table 호출( OTC_SA_CB_DETAIL_UPLOAD )
            sb.append("			select a.*,     																	\n");
            sb.append("                trsact_name_func('SOMO',b.cht_in_out_code,a.trsact_code) as trsact_name,  	\n");
            sb.append("				   b.posting_date, b.cht_in_out_code, b.op_team_code 							\n");
            sb.append("            from OTC_SA_CB_DETAIL_UPLOAD a, otc_sa_head b where a.sa_no = b.sa_no  			\n");
            sb.append(" 			AND A.SA_NO = " + saNo.longValue() + " ");
            //result = commonDao2.getObjects(conn, OTCSaCbDetailDTO.class, sb.toString());
            result = commonDao.getObjects(OTCSaCbDetailUploadDTO.class, sb.toString());
        } else {
            log.error(">>>>>>P_SET_SA_CB_DIFF_AMT_CREATE   작업 시 오류 발생했습니다. ");
            // Error 메시지
            throw new UxbBizException("[System Error] P_SET_SA_CB_DIFF_AMT_CREATE 작업 도중 오류가 발생했습니다.\n시스템 담당자에게 문의 바랍니다.");
        }
        return result;
    }

    /*
     * CB에 의해 배분된 금액과 순수 SOA금액을 최종적으로 확인한다. 100% 일치해야함.
     */
    public String SoaVsCBAmtCheck2(String saNo) {
        String result = null;
        double locDiffAmt = 0;
        double usdDiffAmt = 0;
        long krwDiffAmt = 0;
        double saDurRate = 0;
        StringBuilder sb = new StringBuilder();
        // 2015.01월 데이터 생성작업을  위해서.. 구코드 임시로 넣음..!!(2015.01.28)
        log.debug(" SoaVsCBAmtCheck : \n" + sb.toString());
        int i = 1;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.soaVsCBAmtCheck2", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                saDurRate = Formatter.nullDouble(StringUtil.nvl(map.get("DIFF_RATE_DUR"), "0"));
                locDiffAmt = Formatter.nullDouble(StringUtil.nvl(map.get("LOC_DIFF_AMT"), "0"));
                usdDiffAmt = Formatter.nullDouble(StringUtil.nvl(map.get("USD_DIFF_AMT"), "0"));
                krwDiffAmt = StringUtil.toLong((String) map.get("KRW_DIFF_AMT"), 0L);
                log.debug("saDurRate : " + saDurRate);
                log.debug("locDiffAmt : " + locDiffAmt);
                log.debug("usdDiffAmt : " + usdDiffAmt);
                log.debug("krwDiffAmt : " + krwDiffAmt);
                if (saDurRate != 0 || locDiffAmt != 0 || usdDiffAmt != 0 || krwDiffAmt != 0) {
                    if (saDurRate != 0) {
                        result = "DUR";
                    }
                    if (locDiffAmt != 0) {
                        result = "LOC AMT";
                    }
                    if (usdDiffAmt != 0) {
                        result = "USD AMT";
                    }
                    if (krwDiffAmt != 0) {
                        result = "KRW AMT";
                    }
                } else {
                    result = "";
                }
            }
        }
        return result;
    }

    /*
     * SOA 입력 ITEM 들의 FROM-TO 기간에 대해서, 해당 채산항차 START-END DATE 에 걸치지 않은건 있는지 체크한다.
     * 2014.12.23  HIJANG
     */
    public ArrayList SoaVsCBDurationCheck2(String vslCode, Long VoyNo, String chtInOut, Long stepNo) {
        ArrayList array = null;
        String invalid_item = "";
			/*---------------------------------------------------------------------------------------------------------------------------------------
			// 2015.01월 데이터 생성작업을  위해서.. 구코드 임시로 넣음..!!(2015.01.28)
			// 테스트 데이터
			//sb.append("\n             CB_ALL_VOY_INFO_V CB                                ");
			---------------------------------------------------------------------------------------------------------------------------------------*/
        // GMT 와 LOCAL 시간 - DIFF 차이 처리용 ( hijang 2015.02.09 )
        //----------------------------------------------------------------------------------------------------
        //-- SOA와 채산상의 시간 DIFF(GMT 와 LOCAL 시간) 로 인하여,
        //-- 기존 채산 VOY_STRT_DATE 와 VOY_END_DATE 를 +/- 12H 만큼씩 보정해줌 !! ( HIJANG 2015.02.09 )
        //---------------------------------------------------------------------------------------------------
        //SOA 기간(FROM-TO) 이 채산항차 기간( VOY_STRT_DATE,VOY_END_DATE) 에 포함된 갯수 체크용 ( hijang 2015.02.10 )
        /// 2015.01월 데이터 생성작업을  위해서.. 구코드 임시로 넣음..!!(2015.01.28)

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("VoyNo", VoyNo);
        paramMap.put("chtInOut", chtInOut);
        paramMap.put("stepNo", stepNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.soaVsCBDurationCheck2", paramMap);
        array = new ArrayList<>();
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                invalid_item = String.valueOf(map.get("TRSACT_NAME"));
                //log.debug("invalid_item : "+ invalid_item) ;
                array.add(invalid_item);
            }
        }
        return array;
    }

    public String getCBmaxVoyNo(String vslCode, String cntrNo, String chtInOut) {
        String cbVoy = "";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", vslCode);
        paramMap.put("cntrNo", cntrNo);
        paramMap.put("chtInOut", chtInOut);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.getCBmaxVoyNo", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                cbVoy = String.valueOf(map.get("cb_voy"));
            }
        }
        return cbVoy;
    }

    // Ballast 채산관련 SOA 선급금 생성 후, 다음 STEP 에서 처리
    // 이전STEP에서 발생시킨 선급(J010) 정산여부 체크
    public String isBallastPendingAmt(String saNo, String vslCode, Long voyNo, String posting_date) {
        int cnt2 = 0;
        String result = "";
        String bigResult = "";
        log.debug("saNo : " + saNo);
        log.debug("vslCode : " + vslCode);
        log.debug("voyNo : " + voyNo);
        log.debug("posting_date : " + posting_date);
        String v_trx_number = "";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);
        paramMap.put("vslCode", vslCode);
        paramMap.put("posting_date", posting_date);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.isBallastPendingAmt", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            for (Map<String, Object> map : listMap) {
                v_trx_number = String.valueOf(map.get("TRX_NUMBER"));
                //sb.append("   AND USD_SA_AMT <> 0												\n");

                Map<String, Object> paramMap1 = new HashMap<>();
                paramMap1.put("saNo", saNo);
                paramMap1.put("v_trx_number", v_trx_number);

                List<Map<String, Object>> listMap1 = uxbDAO.select("OTCSADetail.isBallastPendingAmt1", paramMap1);
                if (listMap1 != null && !listMap1.isEmpty()) {
                    cnt2 = Integer.parseInt(StringUtil.nvl(listMap1.get(0).get("item_cnt"), "0"));
                    log.debug("cnt2 : " + cnt2);
                    if (cnt2 > 0) {
                        result = "Y";
                    } else {
                        result = "N";
                    }
                }
                bigResult = bigResult + result;
            }
        }
        return bigResult;
    }

    public String getOnHireMinFromDate(String saNo) {
        String result = "";
        // Query 가져오기
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("saNo", saNo);

        List<Map<String, Object>> listMap = uxbDAO.select("OTCSADetail.getOnHireMinFromDate", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            result = String.valueOf(listMap.get(0).get("min_from_date"));
        }
        return result;
    }

    /*
     * 로컬에서 SOA 채산항차매핑 및 배분작업 후, 자료 생성 및 추출하기 위해 사용한다.
     * SOA DETAIL 데이터 항차 배분 작업 (배치작업용) hijang 2015.01.28
     */
    @Transactional
    public String soaCbDetailDivideCreate(String vslCode, Long voyNo, String chtInCd, Long stepNo, String cntrNo, Long saNo, String ifType, Date cancelPostDate, String cancelReason) {
        String result = "";

        ArrayList array;

        try {
            log.debug("---- soaCbDetailDivideCreate Start.............");

            //OTCSaHeadVO hvo = null;

			/*
			OTCSaHeadDTO saDTO = new OTCSaHeadDTO();
			//CCDAprvReqParamDTO arpDTO = new CCDAprvReqParamDTO() ;
			saDTO.setVsl_code(vslCode);
			saDTO.setVoy_no(voyNo);
			saDTO.setCht_in_out_code(chtInCd);
			saDTO.setStep_no(stepNo);
			hvo = oTCSAHeadDao.saHeadSelect(saDTO);
			*/

            //---------------------------------------------------------------------------------------------------------------
            // 결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - 141222 GYJ
            //---------------------------------------------------------------------------------------------------------------
            log.debug(">> SA_NO : " + saNo + ", VSL_CODE : " + vslCode + ", VOY_NO : " + voyNo + ", Cht_in_out_code : " + chtInCd + ", Step_no : " + stepNo);

            String amtChkresult = "";
            // 컨테이너 선박여부 체크,,
            int cntrVslCnt = this.isContainerVslInfo(vslCode);
            log.debug("cntrVslCnt : " + cntrVslCnt);

            if (cntrVslCnt > 0) {
                // 컨테이너 선박 인 경우 ?
                // --> 배분로직 수행하지 않고, 기존 detail(OTC_SA_DETAIL) 데이터를 이용해서  그대로 I/F 시킨다.
                //details = detailDao.saDetailBySaNoSearch(hvo.getSa_no());
            } else {                // 1) SOA 계약과 선박코드가 채산마스터(CB_ALL_VOY_INFO_ADJUST_V) 에 없는 건인지 체크,,!!
                String cntrVslChk = this.SoaVsCBContractVslCheck(vslCode, cntrNo);
                if (!"".equals(cntrVslChk)) {
                    throw new UxbBizException(cntrVslChk);
                }

                // 2) SOA 입력 ITEM 들의 FROM-TO 기간에 대해서, 해당 채산항차 START-END DATE 에 걸치지 않은건 있는지 체크한다.
                //   --> 해당건 존재시,  무조건 Error 를 발생시켜야 만 한다..!! ( 20141223  HIJANG )
                array = this.SoaVsCBDurationCheck2(vslCode, voyNo, chtInCd, stepNo);

                if (array.size() > 0) {
                    String invalid_item = "";
                    for (int i = 0; i < array.size(); i++) {
                        invalid_item = invalid_item + array.get(i).toString() + ",";
                    }
                    log.debug("invalid_item : " + invalid_item);

                    // Error 메시지
                    throw new UxbBizException(invalid_item.substring(0, invalid_item.length() - 1) + " 항목에 \n채산이 존재하지 않는 SOA 기간이 존재합니다.\n확인 바랍니다.");
                }

                // SOA TEMP TABLE에 배분된 CB,SOA DATA를 DELETE INSERT하는 프로시저 호출 141222 GYJ
                // 2) 컨테이너 선박 아닌 경우 ?
                //    --> 배분로직 수행 및 보정 작업 후, Temp detail(OTC_SA_CB_DETAIL_UPLOAD) 데이터를 이용해서  I/F 시킨다.
                Collection details = this.SoaCBDiffAmtCreateProdedureCall(saNo, vslCode, voyNo, chtInCd, stepNo);

                //최종 확인 : 원금-배분금액 100% 일치여부 (150109 GYJ)
                // OTC_SA_CB_DETAIL_UPLOAD 테이블
                amtChkresult = this.SoaVsCBAmtCheck2(saNo.toString());

                if (!"".equals(amtChkresult) && amtChkresult != null) {
                    String errMsg = "";
                    errMsg = "배분된 " + amtChkresult + " 과  SOA " + amtChkresult + " 이 일치하지 않습니다. 전산실에 문의하여 주십시오 (T.5656/5658) \n";
                    log.debug("errMsg : " + errMsg);

                    // Error 메시지
                    throw new UxbBizException(errMsg);
                }
            }
            //------------------------------------------------------------------

            log.debug("---- soaCbDetailDivideCreate End.............");
        } catch (Exception e) {
            throw new UxbBizException("ERR-0001", e);
        }

        return result;
    }

    /**
     * <p><b>설명 : 컨테이너 선박 여부 체크 2014.12. ryu
     * <p>작성자 :
     *
     * @param vslCode
     * @return String
     */
    public int isContainerVslInfo(String vslCode) {
        int result = 0;
        // Query 가져오기

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vslCode", Formatter.nullTrim(vslCode));

        List<Map<String, Object>> listMap = uxbDAO.select("CCDVslCodeM.isContainerVslInfo", paramMap);
        if (listMap != null && !listMap.isEmpty()) {
            result = Integer.parseInt(String.valueOf(listMap.get(0).get("CNT")));
        }
        return result;
    }
}