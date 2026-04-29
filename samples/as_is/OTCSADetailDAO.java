package com.stx.som.business.dao.sa;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.stx.som.business.dao.bunkerSupply.OBNBnkSplyInvoHeadDAO;
import com.stx.som.business.dao.erp.EARCustomerVendorVDAO;
import com.stx.som.business.dao.erp.ELTTaxXmlDAO;
import com.stx.som.business.dao.standardInfo.CCDTrsactTypeMDAO;
import com.stx.som.business.dao.standardInfo.CCDVslCodeMDAO;
import com.stx.som.business.function.erp.ErpFunction;
import com.stx.som.business.function.sa.StatementAccount;
import com.stx.som.common.dao.PKGenerator;
import com.stx.som.common.dto.erp.EARIfReceiptBalanceVDTO;
import com.stx.som.common.dto.erp.EARVendorBankAccountVDTO;
import com.stx.som.common.dto.sa.OTCBalanceCheckDTO;
import com.stx.som.common.dto.sa.OTCBalanceHeadDTO;
import com.stx.som.common.dto.sa.OTCBrokHeadDTO;
import com.stx.som.common.dto.sa.OTCOwnersComDTO;
import com.stx.som.common.dto.sa.OTCSaAdvancedDTO;
import com.stx.som.common.dto.sa.OTCSaBallstBonusDTO;
import com.stx.som.common.dto.sa.OTCSaBrokerageDTO;
import com.stx.som.common.dto.sa.OTCSaBunkerDTO;
import com.stx.som.common.dto.sa.OTCSaCVEDTO;
import com.stx.som.common.dto.sa.OTCSaCbDetailDTO;
import com.stx.som.common.dto.sa.OTCSaCbDetailUploadDTO;
import com.stx.som.common.dto.sa.OTCSaChartererACDTO;
import com.stx.som.common.dto.sa.OTCSaChatererACSubDTO;
import com.stx.som.common.dto.sa.OTCSaDetailDTO;
import com.stx.som.common.dto.sa.OTCSaHCleanDTO;
import com.stx.som.common.dto.sa.OTCSaHeadDTO;
import com.stx.som.common.dto.sa.OTCSaInterHCleanDTO;
import com.stx.som.common.dto.sa.OTCSaOffHireDTO;
import com.stx.som.common.dto.sa.OTCSaOffHireNegoDTO;
import com.stx.som.common.dto.sa.OTCSaOnHireDTO;
import com.stx.som.common.dto.sa.OTCSaOpenItemDTO;
import com.stx.som.common.dto.sa.OTCSaOwnSettleDTO;
import com.stx.som.common.dto.sa.OTCSaOwnerACDTO;
import com.stx.som.common.dto.sa.OTCSaSpeedClaimDTO;
import com.stx.som.common.dto.sa.OTCSaWithholdingTaxDTO;
import com.stx.som.common.dto.salesOpportunity.SCBVslVoyMDTO;
import com.stx.som.common.dto.standardInfo.CCDTaxDetailDTO;
import com.stx.som.common.exception.STXException;
import com.stx.som.common.utility.DateUtil;
import com.stx.som.common.utility.DbWrap;
import com.stx.som.common.utility.Formatter;
import com.stx.som.common.utility.UserBean;
import com.stx.som.common.vo.erp.EARCustomerVendorVVO;
import com.stx.som.common.vo.sa.OTCSaCbDetailVO;
import com.stx.som.common.vo.sa.OTCSaDetailVO;
import com.stx.som.common.vo.sa.OTCSaHeadVO;
import com.stx.som.common.vo.standardInfo.CCDTrsactTypeMVO;


/**
 * @author yoonsook
 */
public class OTCSADetailDAO {

	/**
	 * <p>
	 * EAD4J의 LogContext 선언
	 */
	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * <p>
	 * 설명: sa Detail 내역을 등록하는 메소드이다.
	 *
	 * @param saDetailVO
	 *            OTCSaDetailVO : Sa Head정보
	 *
	 * @return msgCode String: Sa Detail 테이블에 저장할 시 발생하는 메소드를 리턴한다.
	 *
	 * @exception STXException :
	 *                saDetail Insert 실행하다 발생하는 모든 Exception을 처리한다
	 * @param cpItemDTO
	 * @return
	 */
	public String saDetailInsert(OTCSaDetailVO saDetailVO, UserBean userBean, Connection conn) throws STXException {

		String result = "";
		try {

			DbWrap dbWrap = new DbWrap();

			if (saDetailVO != null) {

				StringBuffer sb = new StringBuffer();
				// Query 가져오기

				sb.append("SELECT V.* FROM OTC_SA_DETAIL V ");
				saDetailVO.setSys_cre_user_id(userBean.getUser_id());
				saDetailVO.setSys_upd_user_id(userBean.getUser_id());
				saDetailVO.setSys_cre_date(new Timestamp(System.currentTimeMillis()));
				saDetailVO.setSys_upd_date(new Timestamp(System.currentTimeMillis()));

				dbWrap.setObject(conn, saDetailVO, sb.toString(), 1);
				result = "SUC-0600";
			}

		} catch (Exception e) {
			throw new STXException(e);
		}
		return result;
	}


	/**
	 * <p>
	 * 설명: sa Detail내역을 등록하는 메소드이다.
	 *
	 * @param saDetailVO
	 *            OTCSaDetailVO : Sa Head정보
	 *
	 * @return msgCode String: Sa Detail 테이블에 저장할 시 발생하는 메소드를 리턴한다.
	 *
	 * @exception STXException :
	 *                saDetail Insert 실행하다 발생하는 모든 Exception을 처리한다
	 * @param cpItemDTO
	 * @return
	 */
	public String saDetailUpdate(OTCSaDetailVO saDetailVO, UserBean userBean, Connection conn) throws STXException {

		String result = "";
		try {
			DbWrap dbWrap = new DbWrap();

			if (saDetailVO != null) {

				StringBuffer sb = new StringBuffer();
				// Query 가져오기

				sb.append("		SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
				sb.append("	          FROM OTC_SA_DETAIL V    ");
				sb.append(" WHERE V.SA_NO = " + Formatter.nullLong(saDetailVO.getSa_no()) + " ");
				sb.append(" AND V.SA_SEQ = " + Formatter.nullLong(saDetailVO.getSa_seq()) + " ");

				saDetailVO.setSys_upd_user_id(userBean.getUser_id());
				saDetailVO.setSys_upd_date(new Timestamp(System.currentTimeMillis()));
				dbWrap.setObject(conn, saDetailVO, sb.toString(), 2);

				result = "SUC-0600";

			}

		} catch (Exception e) {
			throw new STXException(e);
		}

		return result;
	}

	/**
	 * <p>
	 * 설명:sa seq max번호 내역을 조회하는 메소드이다.
	 *
	 * @param sa
	 *            no
	 * @return sa seq max번호
	 * @exception STXException :
	 */
	public Long saSeqMaxNoSelect(Long saNo, Connection conn) throws STXException {

		Long result = null;


		PreparedStatement ps = null;
		ResultSet rs = null;
		long seq = 0;
		try {

			StringBuffer sb = new StringBuffer();
			sb.append(" select max(nvl(sa_seq,0)) +1 as max_seq from otc_sa_detail ");
			sb.append(" where sa_no = " + saNo.longValue() + " ");

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				seq = rs.getLong("max_seq");

			}

			if (seq == 0) {
				result = new Long(1);
			} else {
				result = new Long(seq);
			}

		} catch (Exception e) {

			throw new STXException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e1) {

				throw new STXException(e1);
			}
		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail 내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public OTCSaDetailVO saDetailSelect(Long saNo, Long saSeq, Connection conn) throws STXException {

		OTCSaDetailVO result = null;

		try {
			DbWrap dbWrap = new DbWrap();

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append("		SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("	          FROM OTC_SA_DETAIL V    ");
			sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND SA_SEQ = " + saSeq.longValue() + " ");

			result = (OTCSaDetailVO) dbWrap.getObject(conn, OTCSaDetailVO.class, sb.toString());

		} catch (Exception e) {

			throw new STXException(e);
		}
		return result;
	}

	/**
	 * <p>
	 * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 Charterers A/C 정보를 읽어온다
	 * chtInOutCOde : O, T, R
	 */
	public Collection saChartererACSearch(Long saNo, Long voyNo, Connection conn) throws Exception, STXException {

		Collection result = null;


		PreparedStatement ps = null;
		ResultSet rs = null;

		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		String legal = "";
		String legalVsl = "";
		try {

			// **************************** Charterers' A/C 가져오기 시작
			// **************************** //
			StringBuffer sb = new StringBuffer();

			String detailSql = "";
			detailSql = " SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ";
			detailSql = detailSql + "  FROM OTC_SA_DETAIL V    ";
			sb.append(detailSql);
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			//sb.append(" AND  V.trsact_code IN ('J001', 'J002','J003','J004','J005','J006','J007','J006','J007','J008','J009') ORDER BY V.SA_SEQ ");  // 신규 거래유형(J008,J009) 추가 20150422 hijang
			sb.append(" AND  V.trsact_code IN ('J001', 'J002','J003','J004','J005','J006','J007','J006','J007','J008','J009','J010','J011','J012') ORDER BY V.SA_SEQ ");  // 신규 거래유형(K010) 추가 (20170714 HIJANG) //신규 거래유형(J011)추가 240206 GYJ, J012추가 250212

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			result = new ArrayList();
			Collection chts = new ArrayList();
			OTCSaChartererACDTO chtDTO = null;
			String trsact = "";
			while (rs.next()) {

				chtDTO = new OTCSaChartererACDTO();
				trsact = Formatter.nullTrim(rs.getString("TRSACT_CODE"));

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
					chtDTO.setItem_name("FO SUPPLY BY OWNER");	//명칭변경 20180206 GYJ
				} else if ("J009".equals(trsact)) {
					//chtDTO.setItem_name("BUNKER CHARGE(DO)");
					chtDTO.setItem_name("DO SUPPLY BY OWNER");	//명칭변경 20180206 GYJ
				} else if ("J010".equals(trsact)) {		// 신규 거래유형(K010) 추가 (20170714 HIJANG)
					chtDTO.setItem_name("PREPAYMENT(Ballast)");
				} else if ("J011".equals(trsact)) {		// 신규 거래유형(J011) 추가 (240206)
					chtDTO.setItem_name("PORT CHARGE(EU ETS)");
				} else if ("J012".equals(trsact)) {		// 신규 거래유형(J012) 추가 (250212)
					chtDTO.setItem_name("PORT CHARGE(FUEL EU)");
				}


				chtDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
				chtDTO.setItem(trsact);
				chtDTO.setAmount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
				chtDTO.setAmount_usd(new Double(rs.getDouble("USD_SA_AMT")));
				chtDTO.setRemark(rs.getString("REMARK"));
				chtDTO.setVessel(rs.getString("VSL_CODE"));
				chtDTO.setVoyage(new Long(rs.getLong("VOY_NO")));
				chtDTO.setAccount(rs.getString("CGO_ACC_CODE"));
				chtDTO.setVat_flag(rs.getString("VAT_FLAG"));
				chtDTO.setTax_code(rs.getString("TAX_CODE_FLAG"));
				chtDTO.setOrg_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
				chtDTO.setVat_amount_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
				chtDTO.setVat_amount_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
				chtDTO.setTax_code_name(rs.getString("TAX_CODE_NAME"));
				chtDTO.setBnk_prc(new Double(rs.getDouble("BNK_PRC")));	// 신규 거래유형(I074,I075,J008,J009) 추가 20150422 hijang (BNK_PRC, BNK_QTY 추가)
				chtDTO.setBnk_qty(new Double(rs.getDouble("BNK_QTY")));
				chtDTO.setBnk_type(rs.getString("BNK_TYPE"));	// BUNKER TYPE 추가 (hijang 20200924)

				chts.add(chtDTO);

			}

			result.add(chts);
			// **************************** Charterers' A/C 가져오기 종료
			// **************************** //

			// **************************** Charterers' A/C Item 가져오기 시작
			// **************************** //
			StringBuffer sb1 = new StringBuffer();
			sb1.append(detailSql);
			sb1.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			//sb1.append(" AND  V.trsact_code IN ('K001','K002','K003','K004','K005','K006') ORDER BY V.SA_SEQ ");
			// 일반관리비 은행수수료('630601') -> 고정비 은행수수료('540203') 으로 계정 변경 관련.... 신규 거래유형(K007) 추가 (20160906 HIJANG)
			sb1.append(" AND  V.trsact_code IN ('K001','K002','K003','K004','K005','K006','K007') ORDER BY V.SA_SEQ ");

			ps1 = conn.prepareStatement(sb1.toString());
			rs1 = ps1.executeQuery();

			OTCSaChatererACSubDTO subDTO = new OTCSaChatererACSubDTO();
			String trsactCd = "";

			while (rs1.next()) {
				trsactCd = Formatter.nullTrim(rs1.getString("TRSACT_CODE"));
				if ("K001".equals(trsactCd)) {
					subDTO.setMiss_gain_usd(new Double(rs1.getDouble("USD_SA_AMT")));
				} else if ("K002".equals(trsactCd)) {
					subDTO.setMiss_loss_usd(new Double(rs1.getDouble("USD_SA_AMT")));
				} else if ("K003".equals(trsactCd)) {
					subDTO.setBank_carge_usd(new Double(rs1.getDouble("USD_SA_AMT")));
				// 일반관리비 은행수수료('630601') -> 고정비 은행수수료('540203') 으로 계정 변경 관련.... 신규 거래유형(K007) 추가 (20160906 HIJANG)
				} else if ("K007".equals(trsactCd)) {
					subDTO.setBank_carge_usd(new Double(rs1.getDouble("USD_SA_AMT")));
				} else if ("K004".equals(trsactCd)) {
					subDTO.setArgument_usd(new Double(rs1.getDouble("USD_SA_AMT")));
				} else if ("K005".equals(trsactCd)) {
					legal = "Y";
					subDTO.setLegal_claim_usd(new Double(rs1.getDouble("USD_SA_AMT")));
					subDTO.setLegal_claim_voy(new Long(rs1.getLong("VOY_NO")));
				} else if ("K006".equals(trsactCd)) {
					legalVsl = "Y";
					subDTO.setLegal_claim_vsl_usd(new Double(rs1.getDouble("USD_SA_AMT")));
					subDTO.setLegal_claim_vsl_voy(new Long(rs1.getLong("VOY_NO")));
				}
			}
			if (!"Y".equals(legal))
				subDTO.setLegal_claim_voy(voyNo);
			if (!"Y".equals(legalVsl))
				subDTO.setLegal_claim_vsl_voy(voyNo);

			result.add(subDTO);
			// **************************** Charterers' A/C Item 가져오기 종료
			// **************************** //


		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 Withholding Tax 정보를 읽어온다
	 * chtInOutCOde : O, T, R
	 */
	public OTCSaWithholdingTaxDTO saWithholdingSearch(Long saNo, Connection conn) throws Exception, STXException {

		OTCSaWithholdingTaxDTO result = new OTCSaWithholdingTaxDTO();


		PreparedStatement ps = null;
		ResultSet rs = null;

		PreparedStatement ps1 = null;
		ResultSet rs1 = null;

		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		try {

			// **************************** Vessel Flag 가져오기 시작
			// **************************** //
			StringBuffer sb = new StringBuffer();

			sb.append("   SELECT A.SA_NO, A.WTH_FLAG, A.WTH_ISSUE_DATE, A.WTH_NAT_CODE, ");
			sb.append("			   A.WTH_HIRE_BAL_AMT, A.WTH_HIRE_BAL_REDUCE_RATE, NAT_ENG_NAME_FUNC(A.WTH_NAT_CODE) AS WTH_NAT_NAME ");
			sb.append("		FROM ");
			sb.append("			OTC_SA_HEAD A ");
			sb.append("		WHERE  ");
			sb.append("		  A.SA_NO = ? ");



			ps = conn.prepareStatement(sb.toString());
			ps.setLong(1, saNo.longValue());
			rs = ps.executeQuery();

			String wthFlag = "";

			while (rs.next()) {
				wthFlag = Formatter.nullTrim(rs.getString("WTH_FLAG"));

				result.setSa_no(new Double(rs.getDouble("SA_NO")));
				result.setWth_flag(Formatter.nullTrim(rs.getString("WTH_FLAG")));
				result.setVsl_nat_code(Formatter.nullTrim(rs.getString("WTH_NAT_CODE")));
				result.setOnHire_balance(new Double(rs.getDouble("WTH_HIRE_BAL_AMT")));
				if (rs.getDouble("WTH_HIRE_BAL_REDUCE_RATE") == 0) {
					result.setReduce_amt(new Double(0));
				} else {
					result.setReduce_amt(new Double(1 - rs.getDouble("WTH_HIRE_BAL_REDUCE_RATE")));
				}

				result.setVsl_nat_name(Formatter.nullTrim(rs.getString("WTH_NAT_NAME")));
			}
			// **************************** Vessel Flag 가져오기 종료
			// **************************** //

			// **************************** Withholding Tax 가져오기 시작
			// **************************** //
			if ("Y".equals(wthFlag)) {

				StringBuffer sb1 = new StringBuffer();

				String detailSql = "";
				detailSql = " SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ";
				detailSql = detailSql + "  FROM OTC_SA_DETAIL V    ";

				sb1.append(detailSql);
				sb1.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
				sb1.append(" AND  V.trsact_code IN ('M001', 'M002') ORDER BY V.SA_SEQ ");



				ps1 = conn.prepareStatement(sb1.toString());
				rs1 = ps1.executeQuery();

				String trsactCd = "";
				while (rs1.next()) {

					trsactCd = Formatter.nullTrim(rs1.getString("TRSACT_CODE"));
					if ("M001".equals(trsactCd)) {
						result.setWith_income_check("Y");
						result.setIncome_tax_rate(new Double(rs1.getDouble("SA_RATE")));
						result.setIncome_tax_code(rs1.getString("TAX_CODE_ID"));
						if (rs1.getDouble("SA_RATE") != 0) {
							result.setIncome_tax_base_usd(new Double(Formatter.round(rs1.getDouble("USD_SA_AMT") / (rs1.getDouble("SA_RATE") / 100), 2)));
						}
						result.setIncome_tax_amt_usd(new Double(rs1.getDouble("USD_SA_AMT")));
						result.setIncome_tax_amt_krw(new Double(rs1.getDouble("KRW_SA_AMT")));
						result.setIncome_tax_code_name("");
					} else if ("M002".equals(trsactCd)) {
						result.setWith_inhabit_check("Y");
						result.setInhabit_tax_rate(new Double(rs1.getDouble("SA_RATE")));
						result.setInhabit_tax_code(rs1.getString("TAX_CODE_ID"));
						if (rs1.getDouble("SA_RATE") != 0) {
							result.setInhabit_tax_base_usd(new Double(Formatter.round(rs1.getDouble("USD_SA_AMT") / (rs1.getDouble("SA_RATE") / 100), 2)));
						}
						result.setInhabit_tax_amt_usd(new Double(rs1.getDouble("USD_SA_AMT")));
						result.setInhabit_tax_amt_krw(new Double(rs1.getDouble("KRW_SA_AMT")));
						result.setInhabit_tax_code_name("");
					}

				} // rs while
				// **************************** Withholding Tax 가져오기 종료
				// **************************** //

				// **************************** Tax Base Calculation 가져오기 시작
				// **************************** //
				StringBuffer sb2 = new StringBuffer();

				sb2.append(" SELECT  ");
				sb2.append(" 		    NVL((SELECT SUM(NVL(USD_SA_AMT,0)) ");
				sb2.append(" 		    FROM OTC_SA_DETAIL ");
				sb2.append(" 		    WHERE SA_NO = ?  ");
				//sb2.append(" 	        AND TRSACT_CODE = 'A001' ");
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				sb2.append(" 	        AND TRSACT_CODE IN ('A001','A006') ");
				sb2.append(" 			GROUP BY TRSACT_CODE),0) ON_HIRE, ");
				sb2.append(" 			NVL((SELECT SUM(NVL(USD_SA_AMT,0)) ");
				sb2.append(" 			 FROM OTC_SA_DETAIL ");
				sb2.append(" 			 WHERE SA_NO = ? ");
				//sb2.append(" 			   AND TRSACT_CODE = 'A002' ");
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				sb2.append(" 	        AND TRSACT_CODE IN ('A002','A007') ");
				sb2.append(" 			 GROUP BY TRSACT_CODE),0) ON_HIRE_ADD, ");
				sb2.append(" 			NVL((SELECT SUM(NVL(USD_SA_AMT,0)) AS BONUS ");
				sb2.append(" 			 FROM OTC_SA_DETAIL ");
				sb2.append(" 			 WHERE SA_NO = ?  ");
				//sb2.append(" 			   AND TRSACT_CODE = 'G001' ");
				sb2.append(" 			   AND TRSACT_CODE IN ( 'G001','G003')  ");		//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150204
				sb2.append(" 			 GROUP BY TRSACT_CODE),0) BONUS, 	");
				sb2.append(" 			NVL((SELECT SUM(NVL(USD_SA_AMT,0)) AS BONUSADD ");
				sb2.append(" 			 FROM OTC_SA_DETAIL  ");
				sb2.append(" 			 WHERE SA_NO = ?     ");
				//sb2.append(" 			   AND TRSACT_CODE = 'G002' ");
				sb2.append(" 			   AND TRSACT_CODE IN ( 'G002','G004' ) ");		//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150204
				sb2.append(" 			 GROUP BY TRSACT_CODE),0) BONUS_ADD, ");
				sb2.append(" 			NVL((SELECT SUM(NVL(USD_SA_AMT,0)) AS OFFHIRE ");
				sb2.append(" 			 FROM OTC_SA_DETAIL ");
				sb2.append(" 			 WHERE SA_NO = ?    ");
				//sb2.append(" 			   AND TRSACT_CODE = 'H001' ");
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				sb2.append(" 	        AND TRSACT_CODE IN ('H001','H009') ");
				sb2.append(" 			 GROUP BY TRSACT_CODE),0) OFF_HIRE, ");
				sb2.append(" 			NVL((SELECT SUM(NVL(USD_SA_AMT,0)) AS OFFHIREADD ");
				sb2.append(" 			 FROM OTC_SA_DETAIL ");
				sb2.append(" 			 WHERE SA_NO = ? ");
				//sb2.append(" 			   AND TRSACT_CODE = 'H002' ");
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				sb2.append(" 	        AND TRSACT_CODE IN ('H002','H010') ");
				sb2.append(" 			 GROUP BY TRSACT_CODE),0) OFF_HIRE_ADD, ");
				sb2.append(" 			NVL((SELECT SUM(NVL(USD_SA_AMT,0)) AS SPEED ");
				sb2.append("              FROM OTC_SA_DETAIL ");
				sb2.append(" 		    WHERE SA_NO = ?  ");
				//sb2.append(" 		      AND TRSACT_CODE  IN ('I003', 'I004') ");
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				sb2.append(" 	        AND TRSACT_CODE IN ('I003','I071', 'I004','I072') ");
				sb2.append(" 		    GROUP BY TRSACT_CODE),0) SPEED, ");
				sb2.append(" 		   NVL((SELECT SUM(NVL(USD_SA_AMT,0)) AS SPEEDADD ");
				sb2.append(" 			 FROM OTC_SA_DETAIL  ");
				sb2.append(" 			 WHERE SA_NO = ? ");
				//sb2.append(" 			   AND TRSACT_CODE = 'I005' ");
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				sb2.append(" 	        AND TRSACT_CODE IN ('I005','I073') ");
				sb2.append(" 			 GROUP BY TRSACT_CODE),0) SPEED_CLAIM_ADD ");
				sb2.append(" 		  FROM DUAL ");


				ps2 = conn.prepareStatement(sb2.toString());
				ps2.setLong(1, saNo.longValue());
				ps2.setLong(2, saNo.longValue());
				ps2.setLong(3, saNo.longValue());
				ps2.setLong(4, saNo.longValue());
				ps2.setLong(5, saNo.longValue());
				ps2.setLong(6, saNo.longValue());
				ps2.setLong(7, saNo.longValue());
				ps2.setLong(8, saNo.longValue());
				rs2 = ps2.executeQuery();

				while (rs2.next()) {
					result.setOnHire(new Double(rs2.getDouble("ON_HIRE")));
					result.setOnHire_add_comm(new Double(rs2.getDouble("ON_HIRE_ADD")));
					result.setBonus(new Double(rs2.getDouble("BONUS")));
					result.setBonus_add_comm(new Double(rs2.getDouble("BONUS_ADD")));
					result.setOffHire(new Double(rs2.getDouble("OFF_HIRE")));
					result.setOffHire_add_comm(new Double(rs2.getDouble("OFF_HIRE_ADD")));
					result.setSpeed_Claim(new Double(rs2.getDouble("SPEED")));
					result.setSpeed_Claim_comm(new Double(rs2.getDouble("SPEED_CLAIM_ADD")));

				}
				// **************************** Tax Base Calculation 가져오기 종료
				// **************************** //

			} else {
				result.setWth_flag("N");
				result.setResultMsg("UCG-2010"); // 원천징수 대상 국가가 아닙니다.
			}



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();
			if (rs2 != null)
				rs2.close();
			if (ps2 != null)
				ps2.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public Collection saOwnerACSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo, Connection conn) throws STXException, Exception {

		Collection result = null;


		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps13 = null;
		ResultSet rs13 = null;
		PreparedStatement ps11 = null;
		ResultSet rs11 = null;
		PreparedStatement ps12 = null;
		ResultSet rs12 = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs3 = null;


		boolean brok_check = false;

		try {

			// **************************** Brokerage 가져오기 시작
			// **************************** //
			StringBuffer sb = new StringBuffer();

			sb.append(" SELECT V.*   ,ACC_NAME_FUNC(V.BROK_ACC_CODE) AS BROK_NAME   ");
			sb.append("   FROM OTC_SA_DETAIL V      ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND  V.trsact_code = 'I001' ORDER BY V.SA_SEQ ");



			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaBrokerageDTO brokDTO = new OTCSaBrokerageDTO();
			result = new ArrayList();
			int row = 0;
			String brChk = "";
			String brok11 = "";
			String brok12 = "";
			while (rs.next()) {
				brChk = "Y";
				if (row == 0) {
					brok11 =  Formatter.nullTrim(rs.getString("BROK_ACC_CODE"));
					brokDTO.setBroker(rs.getString("BROK_ACC_CODE"));
					brokDTO.setBroker_name(Formatter.nullTrim(rs.getString("BROK_NAME")));
					brokDTO.setBrokerage_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					brokDTO.setBrokerage_usd(new Double(rs.getDouble("USD_SA_AMT")));
					brokDTO.setComm(new Double(rs.getDouble("SA_RATE")));
					brokDTO.setRemark(rs.getString("REMARK"));
					brokDTO.setBrok_reserve_flag(Formatter.nullTrim(rs.getString("BROK_RESERV_FLAG")));
					brokDTO.setBank_acc_id(Formatter.nullTrim(rs.getString("BANK_ACC_ID")));
					brokDTO.setBank_acc_desc(Formatter.nullTrim(rs.getString("BANK_ACC_DESC")));
				} else if (row == 1) {
					brok12 = Formatter.nullTrim(rs.getString("BROK_ACC_CODE"));
					brokDTO.setBroker2(rs.getString("BROK_ACC_CODE"));
					brokDTO.setBroker_name2(Formatter.nullTrim(rs.getString("BROK_NAME")));
					brokDTO.setBrokerage_krw2(new Double(rs.getDouble("KRW_SA_AMT")));
					brokDTO.setBrokerage_usd2(new Double(rs.getDouble("USD_SA_AMT")));
					brokDTO.setComm2(new Double(rs.getDouble("SA_RATE")));
					brokDTO.setRemark2(rs.getString("REMARK"));
					brokDTO.setBrok_reserve_flag2(Formatter.nullTrim(rs.getString("BROK_RESERV_FLAG")));

					brokDTO.setBank_acc_id2(Formatter.nullTrim(rs.getString("BANK_ACC_ID")));
					brokDTO.setBank_acc_desc2(Formatter.nullTrim(rs.getString("BANK_ACC_DESC")));
				}
				row = row + 1;
			}

			if(row == 1) {

				sb = new StringBuffer();

				sb.append(" SELECT               ");
				sb.append("  				A.CP_ITEM_NO,  ");
				sb.append("  				A.BLST_BONUS,   ");
				sb.append("  				A.CVE,          ");
				sb.append("  				A.VOY_NO ,      ");
				sb.append("  				A.APLY_TIME_FLAG ,  ");
				sb.append("  				A.CNTR_NO  ,        ");
				sb.append("  				A.LAY_CAN_FROM_DATE  , ");
				sb.append("  				A.CNTR_ACC_CODE ,   ");
				sb.append("  				A.LAY_CAN_TO_DATE,  ");
				sb.append("  				A.BANK_NAT_CODE,    ");
				sb.append("  				A.OP_TEAM_CODE ,   ");
				sb.append("  				A.WTH_TAX_FLAG ,   ");
				sb.append("  				A.REDLY_NOTICE1 ,   ");
				sb.append("  				A.BROK_COMM_RATE  ,  ");
				sb.append("  				A.REDLY_NOTICE2,    ");
				sb.append("  				A.BROK_COMM_RATE2 , ");
				sb.append("  				A.REDLY_NOTICE3 ,   ");
				sb.append("  				A.FO_PRICE ,         ");
				sb.append("  				A.REDLY_NOTICE4,    ");
				sb.append("  				A.ILOHC   ,         ");
				sb.append("  				A.REDLY_NOTICE5  ,  ");
				sb.append("  				A.CHT_IN_OUT_CODE,  ");
				sb.append("  				A.REDLY_NOTICE6,    ");
				sb.append("  				A.NAT_CODE,         ");
				sb.append("  				A.REDLY_NOTICE7,    ");
				sb.append("  				A.BROK_ACC_CODE ,   ");
				sb.append("  				A.REDLY_NOTICE8 ,   ");
				sb.append("  				A.REDLY_NOTICE9,     ");
				sb.append("  				A.VSL_CODE,          ");
				sb.append("  				A.REDLY_NOTICE10,    ");
				sb.append("  				A.DO_PRICE ,         ");
				sb.append("  				A.CNTR_TEAM_CODE ,   ");
				sb.append("  				A.BROK_ACC_CODE2,    ");
				sb.append("  				A.SYS_CRE_DATE,     ");
				sb.append("  				A.SYS_CRE_USER_ID , ");
				sb.append("  				A.SYS_UPD_DATE,     ");
				sb.append("				    A.SYS_UPD_USER_ID ,    ");
				sb.append("  				A.BANK_NAME, ");
				sb.append("				NAT_ENG_NAME_FUNC(A.BANK_NAT_CODE) AS BANK_NAT_NAME,		");
				sb.append("				NAT_ENG_NAME_FUNC(A.NAT_CODE) AS NAT_NAME,");
				sb.append("				TEAM_INFO_FUNC(A.OP_TEAM_CODE) AS OP_TEAM_NAME, ");
				sb.append("				TEAM_INFO_FUNC(A.CNTR_TEAM_CODE) AS CNTR_TEAM_NAME, ");
				sb.append("             ACC_NAME_FUNC(A.CNTR_ACC_CODE) AS CNTR_ACC_NAME,");
				sb.append("				ACC_NAME_FUNC(A.BROK_ACC_CODE) AS BROK_ACC_NAME, ");
				sb.append("				ACC_NAME_FUNC(A.BROK_ACC_CODE2) AS BROK_ACC_NAME2,");
				sb.append("				VSL_NAME_FUNC(A.VSL_CODE) AS VSL_NAME, ");
				sb.append("				Cntr_Name_Func(A.CNTR_NO) AS CNTR_NAME, ");
				sb.append("				ACC_NATION_FUNC(A.CNTR_ACC_CODE) AS ACC_NAT_CODE, ");
				sb.append("				ACC_NATION_FUNC(A.BROK_ACC_CODE) AS BROK_NAT, ");
				sb.append("				ACC_NATION_FUNC(A.BROK_ACC_CODE2) AS BROK_NAT2, ");
				sb.append("			    NAT_ENG_NAME_FUNC(ACC_NATION_FUNC(A.CNTR_ACC_CODE)) AS ACC_NAT_NAME ");
				sb.append("			  FROM  OTC_CP_ITEM_HEAD A ");
				sb.append("			  WHERE ");
				sb.append("           A.VSL_CODE = '" + vslCode + "' ");
				sb.append("           AND A.VOY_NO = " + voyNo.longValue() + " ");
				sb.append("           AND A.CHT_IN_OUT_CODE = '" + chtInOutCode + "' ");



				ps3 = conn.prepareStatement(sb.toString());
				rs3 = ps3.executeQuery();

				while (rs3.next()) {


					    if(!brok11.equals(rs3.getString("BROK_ACC_CODE"))  && !"".equals(rs3.getString("BROK_ACC_CODE"))){
					    	if ((!"T".equals(chtInOutCode) && "KR".equals(Formatter.nullTrim(rs3.getString("BROK_NAT")))) ||
					    	   ("KR".equals(Formatter.nullTrim(rs3.getString("ACC_NAT_CODE"))) && "KR".equals(Formatter.nullTrim(rs3.getString("BROK_NAT"))))) {
								// 대선 국내 broker는 prepayment안되게 함, 용선에서 국내 선주, 국내 Broker의 경우 reserved 인되게 함
							} else {

						    	brokDTO.setBroker2(rs3.getString("BROK_ACC_CODE"));
								brokDTO.setBroker_name2(Formatter.nullTrim(rs3.getString("BROK_ACC_NAME")));
								brokDTO.setBrokerage_krw2(new Double(0));
								brokDTO.setBrokerage_usd2(new Double(0));
								brokDTO.setComm2(new Double(rs3.getDouble("BROK_COMM_RATE")));
								brokDTO.setRemark2("");
								brokDTO.setBrok_reserve_flag2("N");
							}
					    }else if(!brok12.equals(rs3.getString("BROK_ACC_CODE2")) && !"".equals(rs3.getString("BROK_ACC_CODE2"))){
					    	if ((!"T".equals(chtInOutCode) && "KR".equals(Formatter.nullTrim(rs3.getString("BROK_NAT2")))) ||
					    	   ("KR".equals(Formatter.nullTrim(rs3.getString("ACC_NAT_CODE"))) && "KR".equals(Formatter.nullTrim(rs3.getString("BROK_NAT2"))))) {
								// 대선 국내 broker는 prepayment안되게 함, 용선에서 국내 선주, 국내 Broker의 경우 reserved 인되게 함
							} else {
						    	brokDTO.setBroker2(rs3.getString("BROK_ACC_CODE2"));
								brokDTO.setBroker_name2(Formatter.nullTrim(rs3.getString("BROK_ACC_NAME2")));
								brokDTO.setBrokerage_krw2(new Double(0));
								brokDTO.setBrokerage_usd2(new Double(0));
								brokDTO.setComm2(new Double(rs3.getDouble("BROK_COMM_RATE2")));
								brokDTO.setRemark2("");
								brokDTO.setBrok_reserve_flag2("N");
							}
					    }

					}//while

					brokDTO.setHire(new Double(0));
					brokDTO.setHire2(new Double(0));
				}




			if ("".equals(brChk)) {

				sb = new StringBuffer();

				sb.append(" SELECT               ");
				sb.append("  				A.CP_ITEM_NO,  ");
				sb.append("  				A.BLST_BONUS,   ");
				sb.append("  				A.CVE,          ");
				sb.append("  				A.VOY_NO ,      ");
				sb.append("  				A.APLY_TIME_FLAG ,  ");
				sb.append("  				A.CNTR_NO  ,        ");
				sb.append("  				A.LAY_CAN_FROM_DATE  , ");
				sb.append("  				A.CNTR_ACC_CODE ,   ");
				sb.append("  				A.LAY_CAN_TO_DATE,  ");
				sb.append("  				A.BANK_NAT_CODE,    ");
				sb.append("  				A.OP_TEAM_CODE ,   ");
				sb.append("  				A.WTH_TAX_FLAG ,   ");
				sb.append("  				A.REDLY_NOTICE1 ,   ");
				sb.append("  				A.BROK_COMM_RATE  ,  ");
				sb.append("  				A.REDLY_NOTICE2,    ");
				sb.append("  				A.BROK_COMM_RATE2 , ");
				sb.append("  				A.REDLY_NOTICE3 ,   ");
				sb.append("  				A.FO_PRICE ,         ");
				sb.append("  				A.REDLY_NOTICE4,    ");
				sb.append("  				A.ILOHC   ,         ");
				sb.append("  				A.REDLY_NOTICE5  ,  ");
				sb.append("  				A.CHT_IN_OUT_CODE,  ");
				sb.append("  				A.REDLY_NOTICE6,    ");
				sb.append("  				A.NAT_CODE,         ");
				sb.append("  				A.REDLY_NOTICE7,    ");
				sb.append("  				A.BROK_ACC_CODE ,   ");
				sb.append("  				A.REDLY_NOTICE8 ,   ");
				sb.append("  				A.REDLY_NOTICE9,     ");
				sb.append("  				A.VSL_CODE,          ");
				sb.append("  				A.REDLY_NOTICE10,    ");
				sb.append("  				A.DO_PRICE ,         ");
				sb.append("  				A.CNTR_TEAM_CODE ,   ");
				sb.append("  				A.BROK_ACC_CODE2,    ");
				sb.append("  				A.SYS_CRE_DATE,     ");
				sb.append("  				A.SYS_CRE_USER_ID , ");
				sb.append("  				A.SYS_UPD_DATE,     ");
				sb.append("				    A.SYS_UPD_USER_ID ,    ");
				sb.append("  				A.BANK_NAME, ");
				sb.append("				NAT_ENG_NAME_FUNC(A.BANK_NAT_CODE) AS BANK_NAT_NAME,		");
				sb.append("				NAT_ENG_NAME_FUNC(A.NAT_CODE) AS NAT_NAME,");
				sb.append("				TEAM_INFO_FUNC(A.OP_TEAM_CODE) AS OP_TEAM_NAME, ");
				sb.append("				TEAM_INFO_FUNC(A.CNTR_TEAM_CODE) AS CNTR_TEAM_NAME, ");
				sb.append("             ACC_NAME_FUNC(A.CNTR_ACC_CODE) AS CNTR_ACC_NAME,");
				sb.append("				ACC_NAME_FUNC(A.BROK_ACC_CODE) AS BROK_ACC_NAME, ");
				sb.append("				ACC_NAME_FUNC(A.BROK_ACC_CODE2) AS BROK_ACC_NAME2,");
				sb.append("				VSL_NAME_FUNC(A.VSL_CODE) AS VSL_NAME, ");
				sb.append("				Cntr_Name_Func(A.CNTR_NO) AS CNTR_NAME, ");
				sb.append("				ACC_NATION_FUNC(A.CNTR_ACC_CODE) AS ACC_NAT_CODE, ");
				sb.append("				ACC_NATION_FUNC(A.BROK_ACC_CODE) AS BROK_NAT, ");
				sb.append("				ACC_NATION_FUNC(A.BROK_ACC_CODE2) AS BROK_NAT2, ");
				sb.append("			    NAT_ENG_NAME_FUNC(ACC_NATION_FUNC(A.CNTR_ACC_CODE)) AS ACC_NAT_NAME ");
				sb.append("			  FROM  OTC_CP_ITEM_HEAD A ");
				sb.append("			  WHERE ");
				sb.append("           A.VSL_CODE = '" + vslCode + "' ");
				sb.append("           AND A.VOY_NO = " + voyNo.longValue() + " ");
				sb.append("           AND A.CHT_IN_OUT_CODE = '" + chtInOutCode + "' ");



				ps13 = conn.prepareStatement(sb.toString());
				rs13 = ps13.executeQuery();

				while (rs13.next()) {

					if ((!"T".equals(chtInOutCode) && "KR".equals(Formatter.nullTrim(rs13.getString("BROK_NAT")))) ||
					    ("KR".equals(Formatter.nullTrim(rs13.getString("ACC_NAT_CODE")))) && "KR".equals(Formatter.nullTrim(rs13.getString("BROK_NAT")))) {
						// 대선 국내 broker는 prepayment안되게 함, 용선에서 국내 선주, 국내 Broker의 경우 reserved 인되게 함
					} else {
						brok_check = true;

						brokDTO.setBroker(rs13.getString("BROK_ACC_CODE"));
						brokDTO.setBroker_name(Formatter.nullTrim(rs13.getString("BROK_ACC_NAME")));
						brokDTO.setBrokerage_krw(new Double(0));
						brokDTO.setBrokerage_usd(new Double(0));
						brokDTO.setComm(new Double(rs13.getDouble("BROK_COMM_RATE")));
						brokDTO.setRemark("");
						brokDTO.setBrok_reserve_flag("N");
					}
					if ((!"T".equals(chtInOutCode) && "KR".equals(Formatter.nullTrim(rs13.getString("BROK_NAT2")))) ||
						("KR".equals(Formatter.nullTrim(rs13.getString("ACC_NAT_CODE")))) && "KR".equals(Formatter.nullTrim(rs13.getString("BROK_NAT2")))) {
						// 대선 국내 broker는 prepayment안되게 함, 용선에서 국내 선주, 국내 Broker의 경우 reserved 인되게 함
					} else {
						if (brok_check) {

							brokDTO.setBroker2(rs13.getString("BROK_ACC_CODE2"));
							brokDTO.setBroker_name2(Formatter.nullTrim(rs13.getString("BROK_ACC_NAME2")));
							brokDTO.setBrokerage_krw2(new Double(0));
							brokDTO.setBrokerage_usd2(new Double(0));
							brokDTO.setComm2(new Double(rs13.getDouble("BROK_COMM_RATE2")));
							brokDTO.setRemark2("");
							brokDTO.setBrok_reserve_flag2("N");

						} else {

							brokDTO.setBroker(rs13.getString("BROK_ACC_CODE2"));
							brokDTO.setBroker_name(Formatter.nullTrim(rs13.getString("BROK_ACC_NAME2")));
							brokDTO.setBrokerage_krw(new Double(0));
							brokDTO.setBrokerage_usd(new Double(0));
							brokDTO.setComm(new Double(rs13.getDouble("BROK_COMM_RATE2")));
							brokDTO.setRemark("");
							brokDTO.setBrok_reserve_flag("N");

						}
					}


				}

				brokDTO.setHire(new Double(0));
				brokDTO.setHire2(new Double(0));

			}

			/*StringBuffer sb12 = new StringBuffer();

			sb12.append("  SELECT (SELECT SUM (USD_SA_AMT) AS ONHIRE ");
			sb12.append(" 					 FROM OTC_SA_DETAIL  ");
			sb12.append("  				  WHERE SA_NO = ? AND TRSACT_CODE = 'A001' ");
			sb12.append("					  GROUP BY TRSACT_CODE) AS ONHIRE from dual ");



			ps12 = conn.prepareStatement(sb12.toString());

			ps12.setLong(1, saNo.longValue());

			rs12 = ps12.executeQuery();

			double hireinit = 0;
			while (rs12.next()) {
				hireinit = rs12.getDouble("ONHIRE");
			}
			if (hireinit <= 0) {
				brokDTO.setHire(new Double(0));
				brokDTO.setHire2(new Double(0));
			} else {*/

				StringBuffer sb11 = new StringBuffer();


				sb11.append("	SELECT ( NVL((SELECT SUM (USD_SA_AMT) AS ONHIRE                 ");
				sb11.append("						  FROM OTC_SA_DETAIL                                  ");
				//sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE = 'A001'    ");
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE IN ( 'A001','A006' )   ");
				sb11.append("						  GROUP BY TRSACT_CODE), 0) +                        ");
				sb11.append("					 NVL((SELECT SUM (USD_SA_AMT) AS OFFHIRE                ");
				sb11.append("            			  FROM OTC_SA_DETAIL                            ");
				//sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE = 'G001'    ");
				sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE IN ( 'G001','G003' )    ");	//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150204
				sb11.append("         				  GROUP BY TRSACT_CODE), 0) -            ");
				sb11.append("					 NVL((SELECT SUM (USD_SA_AMT) AS OFFHIRE                ");
				sb11.append("            			  FROM OTC_SA_DETAIL                            ");
				//sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE = 'H001'    ");
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE IN ( 'H001','H009' )   ");
				sb11.append("         				  GROUP BY TRSACT_CODE), 0) -            ");
				sb11.append(" 					 NVL((SELECT SUM (USD_SA_AMT) AS SPEED         ");
				sb11.append(" 						  FROM OTC_SA_DETAIL                         ");
				//sb11.append(" 						  WHERE SA_NO = ? AND TRSACT_CODE IN ('I003','I004')   ");
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE IN ( 'I003','I071', 'I004','I072' )   ");
				sb11.append(" 						  GROUP BY TRSACT_CODE), 0) +          ");
				//OUT OF HIRE의 경우 BROKER FLAG가 Y일때만 합계에 포함한다.		111201 GYJ
				sb11.append(" 				    NVL((SELECT SUM(USD_SA_AMT) AS OUTOFHIRE                 ");
				sb11.append("						  FROM OTC_SA_DETAIL                                  ");
				//sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE = 'A004'    ");
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				sb11.append("           				  WHERE SA_NO = ? AND TRSACT_CODE IN ( 'A004','A008' )   ");
				sb11.append("                               AND BROK_RESERV_FLAG = 'Y'                       ");
				sb11.append("						  GROUP BY TRSACT_CODE), 0)) AS HIRE    ");
				sb11.append(" 			FROM DUAL                                     ");



				ps11 = conn.prepareStatement(sb11.toString());

				int i = 1;
				ps11.setLong(i++, saNo.longValue());
				ps11.setLong(i++, saNo.longValue());
				ps11.setLong(i++, saNo.longValue());
				ps11.setLong(i++, saNo.longValue());
				ps11.setLong(i++, saNo.longValue());

				rs11 = ps11.executeQuery();

				Double hire = null;
				while (rs11.next()) {
					hire = new Double(rs11.getDouble("HIRE"));
				}
				brokDTO.setHire(hire);
				brokDTO.setHire2(hire);
			////}



			result.add(brokDTO);
			// **************************** Brokerage 가져오기 종료
			// **************************** //

			// **************************** Speed Claim 가져오기 시작
			// **************************** //
			StringBuffer sb1 = new StringBuffer();

			String detailSql = "";
			detailSql = " SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ";
			detailSql = detailSql + "  FROM OTC_SA_DETAIL V    ";
			sb1.append(detailSql);
			sb1.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			//sb1.append(" AND  V.trsact_code IN ('I002','I003','I004','I005','I006','I007','N001','N002') ORDER BY V.GROUP_SEQ ,V.TRSACT_CODE ");
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
			sb1.append(" AND  V.trsact_code IN ('I002', 'I003','I071','I004','I072','I005','I073', 'I006','I007','N001','N002') ORDER BY V.GROUP_SEQ ,V.TRSACT_CODE ");



			ps1 = conn.prepareStatement(sb1.toString());
			rs1 = ps1.executeQuery();

			OTCSaSpeedClaimDTO speedDTO = null;
			Collection speeds = new ArrayList();
			String trsactCd = "";

			row = 0;

			long group_seq = 0;
			long pre_group_seq = 0;
			while (rs1.next()) {

				trsactCd = rs1.getString("TRSACT_CODE");
				group_seq = rs1.getLong("GROUP_SEQ");
				if (row == 0) {
					speedDTO = new OTCSaSpeedClaimDTO();
					pre_group_seq = group_seq;
				} else if (group_seq != pre_group_seq) {
					speeds.add(speedDTO);
					speedDTO = new OTCSaSpeedClaimDTO();
				}

				//if ("I002".equals(trsactCd) || "I003".equals(trsactCd) || "I004".equals(trsactCd)) {
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				if ("I002".equals(trsactCd) || "I003".equals(trsactCd) || "I071".equals(trsactCd) || "I004".equals(trsactCd) || "I072".equals(trsactCd)) {

					speedDTO.setSpeed_claim_flag(rs1.getString("OWN_SPD_CLM_FLAG"));
					speedDTO.setSa_no(new Double(rs1.getDouble("SA_NO")));
					speedDTO.setFrom_date(rs1.getTimestamp("FROM_DATE"));
					speedDTO.setTo_date(rs1.getTimestamp("TO_DATE"));
					speedDTO.setDuration(new Double(rs1.getDouble("SA_RATE_DUR")));
					speedDTO.setDay_hire(new Double(rs1.getDouble("SA_RATE")));
					speedDTO.setFactor(new Double(rs1.getDouble("FACTOR")));
					speedDTO.setSpeed_vat_flag(rs1.getString("VAT_FLAG"));
					speedDTO.setSpeed_tax_code(rs1.getString("TAX_CODE_FLAG"));
					speedDTO.setSpeed_org_vat_no(new Long(rs1.getLong("ORG_VAT_NO")));
					speedDTO.setAmount_usd(new Double(rs1.getDouble("USD_SA_AMT")));
					speedDTO.setAmount_krw(new Double(rs1.getDouble("KRW_SA_AMT")));

					speedDTO.setSpeed_tax_code_name(rs1.getString("TAX_CODE_NAME"));
					speedDTO.setVat_krw(new Double(rs1.getDouble("KRW_VAT_SA_AMT")));
					speedDTO.setVat_usd(new Double(rs1.getDouble("USD_VAT_SA_AMT")));

					speedDTO.setRemark(rs1.getString("REMARK"));

					// 채산 항차 추가 hjkanf 20090602
					speedDTO.setVoyage(new Long(rs1.getLong("VOY_NO")));

					speedDTO.setOrg_factor(new Double(100));



				//} else if ("I005".equals(trsactCd)) {
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				} else if ("I005".equals(trsactCd) || "I073".equals(trsactCd)) {
					speedDTO.setSpeed_claim_flag(rs1.getString("OWN_SPD_CLM_FLAG"));
					speedDTO.setAdd_comm(new Double(rs1.getDouble("SA_RATE")));
					speedDTO.setAdd_comm_amount_usd(new Double(rs1.getDouble("USD_SA_AMT")));
					speedDTO.setAdd_comm_amount_krw(new Double(rs1.getDouble("KRW_SA_AMT")));
					speedDTO.setRsv_factor(new Double(100 - Formatter.nullDouble(speedDTO.getAdd_comm())));
					// 채산 항차 추가 ryu 20100203  add.comm 단독으로 생성하는경우 및 I003과 같이 생성하는 경우 모두 감안
					speedDTO.setVoyage(new Long(rs1.getLong("VOY_NO")));

				} else if ("I006".equals(trsactCd) || "N001".equals(trsactCd)) {
					speedDTO.setSpeed_claim_flag(rs1.getString("OWN_SPD_CLM_FLAG"));
					speedDTO.setFo_qty(new Double(rs1.getDouble("BNK_QTY")));
					speedDTO.setFo_price(new Double(rs1.getDouble("BNK_PRC")));
					speedDTO.setFo_amount_krw(new Double(rs1.getDouble("KRW_SA_AMT")));
					speedDTO.setFo_amount_usd(new Double(rs1.getDouble("USD_SA_AMT")));
					speedDTO.setFo_vat_flag(rs1.getString("VAT_FLAG"));
					speedDTO.setFo_tax_code(rs1.getString("TAX_CODE_FLAG"));
					speedDTO.setFo_org_vat_no(new Long(rs1.getLong("ORG_VAT_NO")));
					speedDTO.setFo_vat_amount_krw(new Double(rs1.getDouble("KRW_VAT_SA_AMT")));
					speedDTO.setFo_vat_amount_usd(new Double(rs1.getDouble("USD_VAT_SA_AMT")));
					speedDTO.setFo_tax_code_name(rs1.getString("TAX_CODE_NAME"));

				} else if ("I007".equals(trsactCd) || "N002".equals(trsactCd)) {
					speedDTO.setSpeed_claim_flag(rs1.getString("OWN_SPD_CLM_FLAG"));
					speedDTO.setDo_qty(new Double(rs1.getDouble("BNK_QTY")));
					speedDTO.setDo_price(new Double(rs1.getDouble("BNK_PRC")));
					speedDTO.setDo_amount_krw(new Double(rs1.getDouble("KRW_SA_AMT")));
					speedDTO.setDo_amount_usd(new Double(rs1.getDouble("USD_SA_AMT")));
					speedDTO.setDo_vat_flag(rs1.getString("VAT_FLAG"));
					speedDTO.setDo_tax_code(rs1.getString("TAX_CODE_FLAG"));
					speedDTO.setDo_org_vat_no(new Long(rs1.getLong("ORG_VAT_NO")));
					speedDTO.setDo_vat_amount_krw(new Double(rs1.getDouble("KRW_VAT_SA_AMT")));
					speedDTO.setDo_vat_amount_usd(new Double(rs1.getDouble("USD_VAT_SA_AMT")));
					speedDTO.setDo_tax_code_name(rs1.getString("TAX_CODE_NAME"));

				}

				row = row + 1;
				pre_group_seq = group_seq;



			}

			if (row > 0) {
				speeds.add(speedDTO);


			}
			result.add(speeds);
			// **************************** Speed Claim 가져오기 종료
			// **************************** //

			// **************************** Owner's A/C 가져오기 시작
			// **************************** //
			StringBuffer sb2 = new StringBuffer();
			sb2.append(detailSql);
			sb2.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb2.append(" AND  V.trsact_code IN ('I008', 'I009','I010','I011','I012','I013','I014','I015','M005','M006', 'I074', 'I075') ORDER BY V.SA_SEQ ");	// 신규 거래유형(I074, I075) 추가 20150422 hijang



			ps2 = conn.prepareStatement(sb2.toString());
			rs2 = ps2.executeQuery();

			OTCSaOwnerACDTO acDTO = null;
			Collection acs = new ArrayList();
			String trsact = "";

			while (rs2.next()) {

				acDTO = new OTCSaOwnerACDTO();
				trsact = Formatter.nullTrim(rs2.getString("TRSACT_CODE"));

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
				} else if ("I074".equals(trsact)) {		// 신규 거래유형(I074, I075) 추가 20150422 hijang
					//acDTO.setItem_name("BUNKER CHARGE(FO)");
					acDTO.setItem_name("FO SUPPLY BY CHARTERER");	//명칭변경 20180206 GYJ
				} else if ("I075".equals(trsact)) {		// 신규 거래유형(I074, I075) 추가 20150422 hijang
					//acDTO.setItem_name("BUNKER CHARGE(DO)");
					acDTO.setItem_name("DO SUPPLY BY CHARTERER");	//명칭변경 20180206 GYJ
				}

				acDTO.setItem(trsact);
				acDTO.setAmount_krw(new Double(rs2.getDouble("KRW_SA_AMT")));
				acDTO.setAmount_usd(new Double(rs2.getDouble("USD_SA_AMT")));
				acDTO.setRemark(rs2.getString("REMARK"));
				acDTO.setVat_flag(rs2.getString("VAT_FLAG"));
				acDTO.setTax_code(rs2.getString("TAX_CODE_FLAG"));
				acDTO.setOrg_vat_no(new Long(rs2.getLong("ORG_VAT_NO")));
				acDTO.setVat_amount_krw(new Double(rs2.getDouble("KRW_VAT_SA_AMT")));
				acDTO.setVat_amount_usd(new Double(rs2.getDouble("USD_VAT_SA_AMT")));
				acDTO.setTax_code_name(rs2.getString("TAX_CODE_NAME"));
				acDTO.setBnk_prc(new Double(rs2.getDouble("BNK_PRC")));	// 신규 거래유형(I074, I075) 추가 20150422 hijang	 (BNK_PRC,  BNK_QTY 추가 )
				acDTO.setBnk_qty(new Double(rs2.getDouble("BNK_QTY")));	// 신규 거래유형(I074, I075) 추가 20150422 hijang	 (BNK_PRC,  BNK_QTY 추가 )
				acDTO.setBnk_type(rs2.getString("BNK_TYPE"));	// BUNKER TYPE 추가 (hijang 20200924)

				// 채산 항차 추가 hjkanf 20090602
				acDTO.setVoyage(new Long(rs2.getLong("VOY_NO")));
				acDTO.setFactor(new Double(rs2.getDouble("FACTOR")));
				acs.add(acDTO);

			}

			result.add(acs);
			// **************************** Owner's A/C 가져오기 종료
			// **************************** //



		} catch (Exception e) {

			throw new STXException(e);
		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (rs2 != null)
				rs2.close();
			if (ps2 != null)
				ps2.close();
			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();
			if (rs11 != null)
				rs11.close();
			if (ps11 != null)
				ps11.close();
			if (rs12 != null)
				rs12.close();
			if (ps12 != null)
				ps12.close();
			if (rs13 != null)
				rs13.close();
			if (ps13 != null)
				ps13.close();
			if (rs3 != null)
				rs3.close();
			if (ps3 != null)
				ps3.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Owner Settle역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 */
	public Collection saOwnerSettleRsvSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo, String stlFlag, String processFlag, Connection conn) throws STXException, Exception {

		Collection result = new ArrayList();
		Collection rev = new ArrayList();
		Collection act = new ArrayList();
		Collection brk = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;

		PreparedStatement ps3 = null;
		ResultSet rs3 = null;
		PreparedStatement ps31 = null;
		ResultSet rs31 = null;
		PreparedStatement ps32 = null;
		ResultSet rs32 = null;


		// String stlFlagCk = "";

		// String sExist = "";

		try {

			String invoice = "";

			invoice = " select a.stl_cntr_acc_code,c.op_team_code, c.cntr_team_code, ";
			invoice = invoice + "     ACC_NAME_FUNC(a.stl_cntr_acc_code) as stl_acc_name , ";
			invoice = invoice + "	 a.curcy_code as currency_code, ";
			invoice = invoice + "	 a.usd_sa_amt as entered_amt, ";
			invoice = invoice + "	 a.usd_sa_amt as usd_amt, ";
			invoice = invoice + "	 a.krw_sa_amt as won_amt, ";
			invoice = invoice + "	 a.stl_erp_slip_no  as slip_no, ";
			invoice = invoice + "	 a.stl_gl_acc_code as gl_acct,  ";
			invoice = invoice + "         c.sa_no, a.stl_flag, a.stl_vsl_code, a.stl_voy_no, a.stl_erp_slip_no, a.usd_sa_amt, ";
			invoice = invoice + "         a.loc_sa_amt, a.krw_sa_amt, a.stl_port_code, a.remark, a.curcy_code, ";
			invoice = invoice + "         a.exc_date, a.exc_rate_type, a.usd_exc_rate, a.loc_exc_rate,a.usd_loc_rate, ";
			invoice = invoice + "        a.due_date, a.terms_date, a.pymt_term, a.pymt_hold_flag ";
			invoice = invoice + "	 from otc_sa_head c, otc_sa_detail a ";
			invoice = invoice + "	 where a.sa_no = c.sa_no ";

			// **************************** Reserved(Owners' Exp) 가져오기 시작
			// **************************** //
			StringBuffer sb = new StringBuffer();
			sb.append(invoice);
			sb.append(" and a.sa_no  = " + saNo.longValue() + " ");
			sb.append(" and a.stl_gl_acc_code IN ('210802' ,'210803' ,'210809') ");

			log.debug(">> saOwnerSettleRsvSearch 쿼리문 \n : " + sb.toString() );

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaOwnSettleDTO settleDTO = null;

			while (rs.next()) {

				settleDTO = new OTCSaOwnSettleDTO();
				settleDTO.setCheck_item("1");
				settleDTO.setOp_team_code(Formatter.nullTrim(rs.getString("op_team_code")));
				settleDTO.setCntr_team_code(Formatter.nullTrim(rs.getString("cntr_team_code")));
				settleDTO.setStl_acc_code(Formatter.nullTrim(rs.getString("stl_cntr_acc_code")));
				settleDTO.setStl_acc_name(Formatter.nullTrim(rs.getString("stl_acc_name")));
				settleDTO.setCurrency_code(Formatter.nullTrim(rs.getString("currency_code")));
				settleDTO.setEntered_amt(new Double(rs.getDouble("entered_amt")));
				settleDTO.setUsd_amt(new Double(rs.getDouble("usd_amt")));
				settleDTO.setWon_amt(new Double(rs.getDouble("won_amt")));
				settleDTO.setSlip_no(Formatter.nullTrim(rs.getString("slip_no")));
				settleDTO.setGl_acct(Formatter.nullTrim(rs.getString("gl_acct")));

				settleDTO.setSa_no(new Double(rs.getDouble("sa_no")));
				settleDTO.setStl_flag(Formatter.nullTrim(rs.getString("stl_flag")));
				// stlFlagCk = settleDTO.getStl_flag();
				settleDTO.setStl_vsl_code(Formatter.nullTrim(rs.getString("stl_vsl_code")));
				settleDTO.setStl_voy_no(new Long(rs.getLong("stl_voy_no")));
				settleDTO.setUsd_sa_amt(new Double(rs.getDouble("usd_sa_amt")));
				settleDTO.setLoc_sa_amt(new Double(rs.getDouble("loc_sa_amt")));
				settleDTO.setKrw_sa_amt(new Double(rs.getDouble("krw_sa_amt")));

				settleDTO.setStl_port_code(Formatter.nullTrim(rs.getString("stl_port_code")));
				settleDTO.setStl_erp_slip_no(Formatter.nullTrim(rs.getString("stl_erp_slip_no")));
				settleDTO.setRemark(Formatter.nullTrim(rs.getString("remark")));
				settleDTO.setCurcy_code(Formatter.nullTrim(rs.getString("curcy_code")));
				settleDTO.setExc_date(rs.getTimestamp("exc_date"));
				//settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs.getTimestamp("exc_date")));   //RYU
				settleDTO.setExchange_rate_date_krw(rs.getTimestamp("exc_date"));
				settleDTO.setExc_rate_type(Formatter.nullTrim(rs.getString("exc_rate_type")));
				settleDTO.setUsd_exc_rate(new Double(rs.getDouble("usd_exc_rate")));
				settleDTO.setLoc_exc_rate(new Double(rs.getDouble("loc_exc_rate")));
				settleDTO.setUsd_loc_rate(new Double(rs.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_usd(new Double(rs.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_krw(new Double(rs.getDouble("loc_exc_rate")));
				settleDTO.setDue_date(rs.getTimestamp("due_date"));
				settleDTO.setPymt_term(Formatter.nullTrim(rs.getString("pymt_term")));
				settleDTO.setTerms_date(rs.getTimestamp("terms_date"));
				settleDTO.setPymt_hold_flag(Formatter.nullTrim(rs.getString("pymt_hold_flag")));

				rev.add(settleDTO);

			}

			result.add(rev);
			// **************************** Reserved(Owners' Exp) 가져오기 종료
			// **************************** //

			// **************************** Actual Owners' A/C(Tc/In) 가져오기 시작
			// **************************** //

			StringBuffer sb2 = new StringBuffer();
			sb2.append(invoice);
			sb2.append(" and a.sa_no  = " + saNo.longValue() + " ");
			sb2.append(" and a.stl_gl_acc_code IN ('110902' ,'110903' ,'110907', '110912', '110913') ");



			ps2 = conn.prepareStatement(sb2.toString());
			rs2 = ps2.executeQuery();

			while (rs2.next()) {
				settleDTO = new OTCSaOwnSettleDTO();
				settleDTO.setCheck_item("1");
				settleDTO.setOp_team_code(Formatter.nullTrim(rs2.getString("op_team_code")));
				settleDTO.setCntr_team_code(Formatter.nullTrim(rs2.getString("cntr_team_code")));
				settleDTO.setStl_acc_code(Formatter.nullTrim(rs2.getString("stl_cntr_acc_code")));
				settleDTO.setStl_acc_name(Formatter.nullTrim(rs2.getString("stl_acc_name")));
				settleDTO.setCurrency_code(Formatter.nullTrim(rs2.getString("currency_code")));
				settleDTO.setEntered_amt(new Double(rs2.getDouble("entered_amt")));
				settleDTO.setUsd_amt(new Double(rs2.getDouble("usd_amt")));
				settleDTO.setWon_amt(new Double(rs2.getDouble("won_amt")));
				settleDTO.setSlip_no(Formatter.nullTrim(rs2.getString("slip_no")));
				settleDTO.setGl_acct(Formatter.nullTrim(rs2.getString("gl_acct")));

				settleDTO.setSa_no(new Double(rs2.getDouble("sa_no")));
				settleDTO.setStl_flag(Formatter.nullTrim(rs2.getString("stl_flag")));
				// stlFlagCk = settleDTO.getStl_flag();
				settleDTO.setStl_vsl_code(Formatter.nullTrim(rs2.getString("stl_vsl_code")));
				settleDTO.setStl_voy_no(new Long(rs2.getLong("stl_voy_no")));
				settleDTO.setUsd_sa_amt(new Double(rs2.getDouble("usd_sa_amt")));
				settleDTO.setLoc_sa_amt(new Double(rs2.getDouble("loc_sa_amt")));
				settleDTO.setKrw_sa_amt(new Double(rs2.getDouble("krw_sa_amt")));
				settleDTO.setStl_port_code(Formatter.nullTrim(rs2.getString("stl_port_code")));
				settleDTO.setStl_erp_slip_no(Formatter.nullTrim(rs2.getString("stl_erp_slip_no")));
				settleDTO.setRemark(Formatter.nullTrim(rs2.getString("remark")));
				settleDTO.setCurcy_code(Formatter.nullTrim(rs2.getString("curcy_code")));
				settleDTO.setExc_date(rs2.getTimestamp("exc_date"));
				//settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs2.getTimestamp("exc_date")));
				settleDTO.setExchange_rate_date_krw(rs2.getTimestamp("exc_date"));    //RYU 2010.07.14
				settleDTO.setExc_rate_type(Formatter.nullTrim(rs2.getString("exc_rate_type")));
				settleDTO.setUsd_exc_rate(new Double(rs2.getDouble("usd_exc_rate")));
				settleDTO.setLoc_exc_rate(new Double(rs2.getDouble("loc_exc_rate")));
				settleDTO.setUsd_loc_rate(new Double(rs2.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_usd(new Double(rs2.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_krw(new Double(rs2.getDouble("loc_exc_rate")));
				settleDTO.setDue_date(rs2.getTimestamp("due_date"));
				settleDTO.setPymt_term(Formatter.nullTrim(rs2.getString("pymt_term")));
				settleDTO.setTerms_date(rs2.getTimestamp("terms_date"));
				settleDTO.setPymt_hold_flag(Formatter.nullTrim(rs2.getString("pymt_hold_flag")));

				act.add(settleDTO);

			}

			result.add(act);
			// **************************** Actual Owners' A/C(Tc/In) 가져오기 종료
			// **************************** //

			// **************************** Reserved(Brokerage) 가져오기 시작
			// **************************** //
			StringBuffer sb1 = new StringBuffer();
			sb1.append(invoice);
			sb1.append(" and a.sa_no  = " + saNo.longValue() + " ");
			sb1.append(" and a.stl_gl_acc_code = '210805'  ");



			ps1 = conn.prepareStatement(sb1.toString());
			rs1 = ps1.executeQuery();

			while (rs1.next()) {
				settleDTO = new OTCSaOwnSettleDTO();
				settleDTO.setCheck_item("1");
				settleDTO.setOp_team_code(Formatter.nullTrim(rs1.getString("op_team_code")));
				settleDTO.setCntr_team_code(Formatter.nullTrim(rs1.getString("cntr_team_code")));
				settleDTO.setStl_acc_code(Formatter.nullTrim(rs1.getString("stl_cntr_acc_code")));
				settleDTO.setStl_acc_name(Formatter.nullTrim(rs1.getString("stl_acc_name")));
				settleDTO.setCurrency_code(Formatter.nullTrim(rs1.getString("currency_code")));
				settleDTO.setEntered_amt(new Double(rs1.getDouble("entered_amt")));
				settleDTO.setUsd_amt(new Double(rs1.getDouble("usd_amt")));
				settleDTO.setWon_amt(new Double(rs1.getDouble("won_amt")));
				settleDTO.setSlip_no(Formatter.nullTrim(rs1.getString("slip_no")));
				settleDTO.setGl_acct(Formatter.nullTrim(rs1.getString("gl_acct")));

				settleDTO.setSa_no(new Double(rs1.getDouble("sa_no")));
				settleDTO.setStl_flag(Formatter.nullTrim(rs1.getString("stl_flag")));
				// stlFlagCk = settleDTO.getStl_flag();
				settleDTO.setStl_vsl_code(Formatter.nullTrim(rs1.getString("stl_vsl_code")));
				settleDTO.setStl_voy_no(new Long(rs1.getLong("stl_voy_no")));
				settleDTO.setUsd_sa_amt(new Double(rs1.getDouble("usd_sa_amt")));
				settleDTO.setLoc_sa_amt(new Double(rs1.getDouble("loc_sa_amt")));
				settleDTO.setKrw_sa_amt(new Double(rs1.getDouble("krw_sa_amt")));
				settleDTO.setStl_port_code(Formatter.nullTrim(rs1.getString("stl_port_code")));
				settleDTO.setStl_erp_slip_no(Formatter.nullTrim(rs1.getString("stl_erp_slip_no")));
				settleDTO.setRemark(Formatter.nullTrim(rs1.getString("remark")));
				settleDTO.setCurcy_code(Formatter.nullTrim(rs1.getString("curcy_code")));
				settleDTO.setExc_date(rs1.getTimestamp("exc_date"));
				//settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs1.getTimestamp("exc_date")));
				settleDTO.setExchange_rate_date_krw(rs1.getTimestamp("exc_date"));    //RYU 2010.07.14
				settleDTO.setExc_rate_type(Formatter.nullTrim(rs1.getString("exc_rate_type")));
				settleDTO.setUsd_exc_rate(new Double(rs1.getDouble("usd_exc_rate")));
				settleDTO.setLoc_exc_rate(new Double(rs1.getDouble("loc_exc_rate")));
				settleDTO.setUsd_loc_rate(new Double(rs1.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_usd(new Double(rs1.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_krw(new Double(rs1.getDouble("loc_exc_rate")));
				settleDTO.setDue_date(rs1.getTimestamp("due_date"));
				settleDTO.setPymt_term(Formatter.nullTrim(rs1.getString("pymt_term")));
				settleDTO.setTerms_date(rs1.getTimestamp("terms_date"));
				settleDTO.setPymt_hold_flag(Formatter.nullTrim(rs1.getString("pymt_hold_flag")));

				brk.add(settleDTO);

			}

			result.add(brk);
			// **************************** Reserved(Brokerage) 가져오기 종료
			// **************************** //



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();
			if (rs2 != null)
				rs2.close();
			if (ps2 != null)
				ps2.close();
			if (rs3 != null)
				rs3.close();
			if (ps3 != null)
				ps3.close();
			if (rs31 != null)
				rs31.close();
			if (ps31 != null)
				ps31.close();
			if (rs32 != null)
				rs32.close();
			if (ps32 != null)
				ps32.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Owner Settle역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 */
	public Collection saOwnerSettleApSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo, String stlFlag, String processFlag, Connection conn) throws STXException, Exception {

		Collection result = new ArrayList();
		Collection rev = new ArrayList();
		Collection act = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;

		PreparedStatement ps3 = null;
		ResultSet rs3 = null;
		PreparedStatement ps31 = null;
		ResultSet rs31 = null;
		// String stlFlagCk = "";


		try {

			// **************************** Account payable / Advance Received
			// 가져오기 시작 **************************** //
			String invoice = "";

			invoice = " select a.stl_cntr_acc_code,c.op_team_code, c.cntr_team_code, ";
			invoice = invoice + "     ACC_NAME_FUNC(a.stl_cntr_acc_code) as stl_acc_name , ";
			invoice = invoice + "	 a.curcy_code as currency_code, ";
			invoice = invoice + "	 a.usd_sa_amt as entered_amt, ";
			invoice = invoice + "	 a.usd_sa_amt as usd_amt, ";
			invoice = invoice + "	 a.krw_sa_amt as won_amt, ";
			invoice = invoice + "	 a.stl_erp_slip_no  as slip_no, ";
			invoice = invoice + "	 a.stl_gl_acc_code as gl_acct,  ";
			invoice = invoice + "         c.sa_no, a.stl_flag, a.stl_vsl_code, a.stl_voy_no, a.stl_erp_slip_no, a.usd_sa_amt, ";
			invoice = invoice + "         a.loc_sa_amt, a.krw_sa_amt, a.stl_port_code, a.remark, a.curcy_code, ";
			invoice = invoice + "         a.exc_date, a.exc_rate_type, a.usd_exc_rate, a.loc_exc_rate,a.usd_loc_rate, ";
			invoice = invoice + "        a.due_date, a.terms_date, a.pymt_term, a.pymt_hold_flag ";
			invoice = invoice + "	 from otc_sa_head c, otc_sa_detail a ";
			invoice = invoice + "	 where a.sa_no = c.sa_no ";

			StringBuffer sb = new StringBuffer();
			sb.append(invoice);
			sb.append(" and a.sa_no  = " + saNo.longValue() + " ");
			sb.append(" and a.stl_gl_acc_code IN ('210403' ,'210405' ,'210402','210701','210499') ");	//111013 GYJ 110599(기타 영업미지급금) 추가.

			log.debug(">> saOwnerSettleApSearch 쿼리문 \n : " + sb.toString() );

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaOwnSettleDTO settleDTO = null;

			while (rs.next()) {
				settleDTO = new OTCSaOwnSettleDTO();
				settleDTO.setCheck_item("1");
				settleDTO.setOp_team_code(Formatter.nullTrim(rs.getString("op_team_code")));
				settleDTO.setCntr_team_code(Formatter.nullTrim(rs.getString("cntr_team_code")));
				settleDTO.setStl_acc_code(Formatter.nullTrim(rs.getString("stl_cntr_acc_code")));
				settleDTO.setStl_acc_name(Formatter.nullTrim(rs.getString("stl_acc_name")));
				settleDTO.setCurrency_code(Formatter.nullTrim(rs.getString("currency_code")));
				settleDTO.setEntered_amt(new Double(rs.getDouble("entered_amt")));
				settleDTO.setUsd_amt(new Double(rs.getDouble("usd_amt")));
				settleDTO.setWon_amt(new Double(rs.getDouble("won_amt")));
				settleDTO.setSlip_no(Formatter.nullTrim(rs.getString("slip_no")));
				settleDTO.setGl_acct(Formatter.nullTrim(rs.getString("gl_acct")));

				settleDTO.setSa_no(new Double(rs.getDouble("sa_no")));
				settleDTO.setStl_flag(Formatter.nullTrim(rs.getString("stl_flag")));
				// stlFlagCk = settleDTO.getStl_flag();
				settleDTO.setStl_vsl_code(Formatter.nullTrim(rs.getString("stl_vsl_code")));
				settleDTO.setStl_voy_no(new Long(rs.getLong("stl_voy_no")));
				settleDTO.setUsd_sa_amt(new Double(rs.getDouble("usd_sa_amt")));
				settleDTO.setLoc_sa_amt(new Double(rs.getDouble("loc_sa_amt")));
				settleDTO.setKrw_sa_amt(new Double(rs.getDouble("krw_sa_amt")));
				settleDTO.setStl_port_code(Formatter.nullTrim(rs.getString("stl_port_code")));
				settleDTO.setStl_erp_slip_no(Formatter.nullTrim(rs.getString("stl_erp_slip_no")));
				settleDTO.setRemark(Formatter.nullTrim(rs.getString("remark")));
				settleDTO.setCurcy_code(Formatter.nullTrim(rs.getString("curcy_code")));
				settleDTO.setExc_date(rs.getTimestamp("exc_date"));
				//settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs.getTimestamp("exc_date")));
				settleDTO.setExchange_rate_date_krw(rs.getTimestamp("exc_date"));    //RYU 2010.07.14
				settleDTO.setExc_rate_type(Formatter.nullTrim(rs.getString("exc_rate_type")));
				settleDTO.setUsd_exc_rate(new Double(rs.getDouble("usd_exc_rate")));
				settleDTO.setLoc_exc_rate(new Double(rs.getDouble("loc_exc_rate")));
				settleDTO.setUsd_loc_rate(new Double(rs.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_usd(new Double(rs.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_krw(new Double(rs.getDouble("loc_exc_rate")));
				settleDTO.setDue_date(rs.getTimestamp("due_date"));
				settleDTO.setPymt_term(Formatter.nullTrim(rs.getString("pymt_term")));
				settleDTO.setTerms_date(rs.getTimestamp("terms_date"));
				settleDTO.setPymt_hold_flag(Formatter.nullTrim(rs.getString("pymt_hold_flag")));

				rev.add(settleDTO);

			}

			result.add(rev);
			// **************************** Account payable / Advance Received
			// 가져오기 종료 **************************** //

			// **************************** Account Receivable 가져오기 시작
			// **************************** //
			StringBuffer sb2 = new StringBuffer();
			sb2.append(invoice);
			sb2.append(" and a.sa_no  = " + saNo.longValue() + " ");
			sb2.append(" and a.stl_gl_acc_code IN ('110502' ,'110503' ,'110599') ");		//111013 GYJ 110599(영업미수-기타잔액) 추가.



			ps2 = conn.prepareStatement(sb2.toString());
			rs2 = ps2.executeQuery();

			while (rs2.next()) {
				settleDTO = new OTCSaOwnSettleDTO();
				settleDTO.setCheck_item("1");
				settleDTO.setOp_team_code(Formatter.nullTrim(rs2.getString("op_team_code")));
				settleDTO.setCntr_team_code(Formatter.nullTrim(rs2.getString("cntr_team_code")));
				settleDTO.setStl_acc_code(Formatter.nullTrim(rs2.getString("stl_cntr_acc_code")));
				settleDTO.setStl_acc_name(Formatter.nullTrim(rs2.getString("stl_acc_name")));
				settleDTO.setCurrency_code(Formatter.nullTrim(rs2.getString("currency_code")));
				settleDTO.setEntered_amt(new Double(rs2.getDouble("entered_amt")));
				settleDTO.setUsd_amt(new Double(rs2.getDouble("usd_amt")));
				settleDTO.setWon_amt(new Double(rs2.getDouble("won_amt")));
				settleDTO.setSlip_no(Formatter.nullTrim(rs2.getString("slip_no")));
				settleDTO.setGl_acct(Formatter.nullTrim(rs2.getString("gl_acct")));

				settleDTO.setSa_no(new Double(rs2.getDouble("sa_no")));
				settleDTO.setStl_flag(Formatter.nullTrim(rs2.getString("stl_flag")));
				// stlFlagCk = settleDTO.getStl_flag();
				settleDTO.setStl_vsl_code(Formatter.nullTrim(rs2.getString("stl_vsl_code")));
				settleDTO.setStl_voy_no(new Long(rs2.getLong("stl_voy_no")));
				settleDTO.setUsd_sa_amt(new Double(rs2.getDouble("usd_sa_amt")));
				settleDTO.setLoc_sa_amt(new Double(rs2.getDouble("loc_sa_amt")));
				settleDTO.setKrw_sa_amt(new Double(rs2.getDouble("krw_sa_amt")));
				settleDTO.setStl_port_code(Formatter.nullTrim(rs2.getString("stl_port_code")));
				settleDTO.setStl_erp_slip_no(Formatter.nullTrim(rs2.getString("stl_erp_slip_no")));
				settleDTO.setRemark(Formatter.nullTrim(rs2.getString("remark")));
				settleDTO.setCurcy_code(Formatter.nullTrim(rs2.getString("curcy_code")));
				settleDTO.setExc_date(rs2.getTimestamp("exc_date"));
				//settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs2.getTimestamp("exc_date")));
				settleDTO.setExchange_rate_date_krw(rs2.getTimestamp("exc_date"));   //RYU 2010.07.14
				settleDTO.setExc_rate_type(Formatter.nullTrim(rs2.getString("exc_rate_type")));
				settleDTO.setUsd_exc_rate(new Double(rs2.getDouble("usd_exc_rate")));
				settleDTO.setLoc_exc_rate(new Double(rs2.getDouble("loc_exc_rate")));
				settleDTO.setUsd_loc_rate(new Double(rs2.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_usd(new Double(rs2.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_krw(new Double(rs2.getDouble("loc_exc_rate")));
				settleDTO.setDue_date(rs2.getTimestamp("due_date"));
				settleDTO.setPymt_term(Formatter.nullTrim(rs2.getString("pymt_term")));
				settleDTO.setTerms_date(rs2.getTimestamp(("terms_date")));
				settleDTO.setPymt_hold_flag(Formatter.nullTrim(rs2.getString("pymt_hold_flag")));

				act.add(settleDTO);

			}

			result.add(act);
			// **************************** Account Receivable 가져오기 종료
			// **************************** //



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();
			if (rs2 != null)
				rs2.close();
			if (ps2 != null)
				ps2.close();
			if (rs3 != null)
				rs3.close();
			if (ps3 != null)
				ps3.close();
			if (rs31 != null)
				rs31.close();
			if (ps31 != null)
				ps31.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public Collection saOnHireSelect(Long saNo, Long orgSaNo, String amdCode, Connection conn) throws STXException, Exception {

		Collection result = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			// **************************** OnHire 가져오기 시작
			// **************************** //
			StringBuffer sb = new StringBuffer();

			sb.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			//sb.append(" AND  V.trsact_code IN ('A001' , 'A002') ORDER BY V.GROUP_SEQ , V.TRSACT_CODE ");
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
			sb.append(" AND  V.trsact_code IN ('A001', 'A002', 'A006', 'A007') ORDER BY V.GROUP_SEQ , V.TRSACT_CODE ");

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaOnHireDTO onHireDTO = null;
			Collection hires = new ArrayList();

			int row = 0;
			long group_seq = 0;
			long pre_group_seq = 0;
			while (rs.next()) {
				// TRSACT_CODE인 A001와 A002는 한쌍으로 존재 할때만 collection(hires)에 담는다.

				group_seq = rs.getLong("GROUP_SEQ");
				if (row == 0) {
					onHireDTO = new OTCSaOnHireDTO();
					pre_group_seq = group_seq;
				} else if (group_seq != pre_group_seq) {
					hires.add(onHireDTO);
					onHireDTO = new OTCSaOnHireDTO();
				}

				//if ("A001".equals(rs.getString("TRSACT_CODE"))) {
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				if ("A001".equals(rs.getString("TRSACT_CODE")) || "A006".equals(rs.getString("TRSACT_CODE"))) {

					onHireDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					onHireDTO.setFrom_date(rs.getTimestamp("FROM_DATE"));
					onHireDTO.setTo_date(rs.getTimestamp("TO_DATE"));
					onHireDTO.setDur(new Double(rs.getDouble("SA_RATE_DUR")));
					onHireDTO.setDay_hire(new Double(rs.getDouble("SA_RATE")));
					onHireDTO.setAmount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					onHireDTO.setAmount_krw(new Double(rs.getDouble("KRW_SA_AMT")));

					// bbc 처리 관련 vat 정보 setting hjkang 20090813
					onHireDTO.setVat_flag(rs.getString("VAT_FLAG"));
					onHireDTO.setTax_code(rs.getString("TAX_CODE_FLAG"));
					onHireDTO.setOrg_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					onHireDTO.setTax_code_name(rs.getString("TAX_CODE_NAME"));
					onHireDTO.setVat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					onHireDTO.setVat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
				}

				//if ("A002".equals(rs.getString("TRSACT_CODE"))) {
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				if ("A002".equals(rs.getString("TRSACT_CODE")) || "A007".equals(rs.getString("TRSACT_CODE"))) {
					onHireDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					onHireDTO.setAdd_comm(new Double(rs.getDouble("SA_RATE")));
					onHireDTO.setAdd_comm_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					onHireDTO.setAdd_comm_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));

				}

				row = row + 1;
				pre_group_seq = group_seq;

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
			ps.close();
			rs.close();

			sb.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND  V.trsact_code ='A003' ORDER BY V.SA_SEQ ");

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaCVEDTO cveDTO = null;
			Collection cves = new ArrayList();

			while (rs.next()) {

				cveDTO = new OTCSaCVEDTO();
				cveDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
				cveDTO.setVat_flag(rs.getString("VAT_FLAG"));
				cveDTO.setTax_code(rs.getString("TAX_CODE_FLAG"));
				cveDTO.setOrg_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
				cveDTO.setTax_code_name(rs.getString("TAX_CODE_NAME"));
				cveDTO.setAmount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
				cveDTO.setAmount_usd(new Double(rs.getDouble("USD_SA_AMT")));
				cveDTO.setVat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
				cveDTO.setVat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
				cveDTO.setRemark(rs.getString("REMARK"));
				//170323 GYJ
				cveDTO.setCve_flag(rs.getString("CVE_FLAG"));
				cveDTO.setFrom_date(rs.getTimestamp("FROM_DATE"));
				cveDTO.setTo_date(rs.getTimestamp("TO_DATE"));

				cves.add(cveDTO);
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
				ps.close();
				rs.close();

				// 수정세금계산서라면 원본 SA_NO  읽어오기
		        sb.append(" SELECT NVL(MAX(T.ORG_TRX_NO), 0) AS ORG_TRX_NO, NVL(MAX(T.AMD_CODE), 'X') AS AMD_CODE       ");
		        sb.append("    FROM OTC_AMD_TAX_INFO T    		       ");
				sb.append("  WHERE T.AMD_TRX_NO = " + saNo.longValue() + " ");
				sb.append("  AND T.DEL_FLAG = 'N'                                                 ");

				log.debug("원본 sa_no, amd 가져오기"+ sb.toString());
				ps = conn.prepareStatement(sb.toString());
				rs = ps.executeQuery();

				while (rs.next()) {
					org_sa_no = new Long(rs.getLong("ORG_TRX_NO")).longValue();
					amd_code = rs.getString("AMD_CODE");
				}

			} else { // 수정세금계산서로 신규 발행하는 건으로 Open 하였으므로 연계 번호를 그대로 읽어온다.
				log.debug("orgSaNo not null or not o 이면 param으로 가져오기 ");
				org_sa_no = Formatter.nullZero(orgSaNo).longValue();
				amd_code = amdCode;
			}

			Collection orgSaInfo = new ArrayList();
			Collection orgTaxInfo = new ArrayList();

			if(org_sa_no == 0) { // 수정세금계산서가 존재하지 않으면 아래 로직을 불필요
				result.add(orgSaInfo);  // 원본 master 정보
				result.add(orgTaxInfo);  // 원본 tax list 정보

			} else {  // // 수정 세금계산서가 존재 할시 start

				sb.setLength(0);
				ps.close();
				rs.close();

				sb.append(" SELECT A.SA_NO, A.POSTING_DATE, A.LOC_EXC_RATE    ");
				sb.append(" FROM OTC_SA_HEAD A			         ");
				sb.append(" WHERE A.SA_NO = " + org_sa_no + "	         ");

				log.debug("원본 sa_no, exc rate, gl_date 가져오기"+ sb.toString());

				ps = conn.prepareStatement(sb.toString());
				rs = ps.executeQuery();

				OTCSaHeadDTO orgSaDTO = null;


				while (rs.next()) {

					orgSaDTO = new OTCSaHeadDTO();

					orgSaDTO.setSa_no(new Long(rs.getLong("SA_NO")));
					orgSaDTO.setPosting_date(rs.getTimestamp("POSTING_DATE"));
					orgSaDTO.setLoc_exc_rate(new Double(rs.getDouble("LOC_EXC_RATE")));
					orgSaDTO.setAmend_code(amd_code);   // 수정 코드

					orgSaInfo.add(orgSaDTO);
				}

				result.add(orgSaInfo);

				//	**************************** 수정세금계산서 원본 List 가져오기 시작
				// **************************** //

				sb.setLength(0);
				ps.close();
				rs.close();

				sb.append("\n    SELECT VAT_NO, S_SEQ, SEQ, CNT, S_TITLE, ITEM_DESC, TRSACT_CODE, TAX_CODE_ID, KRW_BASE_AMT, KRW_VAT_AMT   ");
				sb.append("\n    FROM 															     ");
				sb.append("\n      (																    ");
				sb.append("\n      SELECT  D.VAT_NO, D.S_SEQ, D.SEQ, MAX(CNT) CNT, 								    ");
				sb.append("\n            DECODE(D.SEQ, 2, 'S.TOTAL', MAX(D.VAT_INVOICE_NO)) S_TITLE,						    ");
				sb.append("\n            DECODE(D.SEQ, 2, ' ', MAX(D.ITEM_DESC)) ITEM_DESC, 							     ");
				sb.append("\n            DECODE(D.SEQ, 2, ' ', MAX(D.TRSACT_CODE)) TRSACT_CODE, 						    ");
				sb.append("\n            DECODE(D.SEQ, 2, ' ', MAX(D.TAX_CODE_ID)) TAX_CODE_ID,						    ");
				sb.append("\n            DECODE(D.SEQ, 2, SUM(D.KRW_BASE_AMT), MAX(D.KRW_BASE_AMT)) AS KRW_BASE_AMT,		     ");
				sb.append("\n            DECODE(D.SEQ, 2, SUM(D.KRW_VAT_AMT), MAX(D.KRW_VAT_AMT)) AS KRW_VAT_AMT      	     ");
				sb.append("\n      FROM															     ");
				sb.append("\n        (																    ");
				sb.append("\n        SELECT M.*, S.SEQ, DECODE(S.SEQ, 1, M.VAT_SEQ, 9999) S_SEQ						    ");
				sb.append("\n        FROM 															     ");
				sb.append("\n        (																    ");
				sb.append("\n        SELECT TH.VAT_NO, TH.VAT_INVOICE_NO, TD.VAT_SEQ, TD.ITEM_DESC, TD.TRSACT_CODE, 		    ");
				sb.append("\n               SUBSTR(TH.TAX_CODE_ID, 4, 1) AS TAX_CODE_ID,									    ");
				sb.append("\n               TD.KRW_BASE_AMT, TD.KRW_VAT_AMT,									    ");
				sb.append("\n               MAX(TD.VAT_SEQ) OVER (PARTITION BY TH.DOCU_NO) CNT						    ");
				sb.append("\n        FROM CCD_TAX_HEAD TH, CCD_TAX_DETAIL TD									     ");
				sb.append("\n        WHERE TH.VAT_NO = TD.VAT_NO											    ");
				sb.append("\n        AND TH.DEL_FLAG = 'N'												    ");
				sb.append("\n        AND TH.VAT_TYPE_CODE = 'S1'											    ");
				sb.append("\n        AND TH.DOCU_NO = '" + org_sa_no + "'						    ");
				sb.append("\n        ) M,															    ");
				sb.append("\n        SOM_SEQ_DUAL S													     ");
				sb.append("\n        WHERE S.SEQ <= 2													    ");
				sb.append("\n        ) D																     ");
				sb.append("\n      GROUP BY D.VAT_NO, D.S_SEQ, D.SEQ										    ");
				sb.append("\n      ) 																    ");
				sb.append("\n    WHERE SEQ = 1 OR (SEQ = 2 AND CNT > 1)										     ");
				sb.append("\n    ORDER BY VAT_NO, SEQ, S_SEQ 											    ");

				log.debug("원본 tax list  가져오기"+ sb.toString());

				ps = conn.prepareStatement(sb.toString());
				rs = ps.executeQuery();

				CCDTaxDetailDTO taxDTO = null;


				while (rs.next()) {

					taxDTO = new CCDTaxDetailDTO();

					taxDTO.setVat_no(new Long(rs.getLong("VAT_NO")));
					taxDTO.setVat_seq(new Long(rs.getLong("S_SEQ")));
					taxDTO.setSa_seq(new Long(rs.getLong("SEQ")));
					taxDTO.setTrsact_name(rs.getString("S_TITLE"));
					taxDTO.setItem_desc(rs.getString("ITEM_DESC"));
					taxDTO.setTrsact_code(rs.getString("TRSACT_CODE"));
					taxDTO.setTax_code_id(rs.getString("TAX_CODE_ID"));
					taxDTO.setKrw_base_amt(new Long(rs.getLong("KRW_BASE_AMT")));
					taxDTO.setKrw_vat_amt(new Long(rs.getLong("KRW_VAT_AMT")));
					orgTaxInfo.add(taxDTO);
				}

				result.add(orgTaxInfo);
				//			**************************** 수정세금계산서 원본 List 가져오기 종료
				// **************************** //
			}  // 수정 세금계산서가 존재 할시 end


			//	**************************** 저장된 선수금 gl_date 가져오기 시작 (2011.03.08 GYJ)
			// **************************** //

			sb.setLength(0);
			ps.close();
			rs.close();

			sb.append("	select v.gl_date adv_gl_date						\n");
			sb.append("	 from EAR_IF_receipt_BALANCE_V V, OTC_SA_DETAIL D	\n");
			sb.append("	 WHERE V.RECEIPT_NUMBER = D.STL_ERP_SLIP_NO			\n");
			sb.append("	 AND D.SA_NO = "+saNo+"							\n");
			sb.append("	 AND D.STL_GL_ACC_CODE = 210701					\n");

			log.debug("저장된 선수금 gl_date 가져오기"+ sb.toString());

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			EARIfReceiptBalanceVDTO RecieptDTO = null;
			Collection reciepts = new ArrayList();

			while(rs.next()){
				RecieptDTO = new EARIfReceiptBalanceVDTO();

				RecieptDTO.setGl_Date(rs.getTimestamp("adv_gl_date"));

				reciepts.add(RecieptDTO);

			}

			result.add(reciepts);

			//	**************************** 저장된 선수금 gl_date 가져오기 종료
			// **************************** //

			//	**************************** 저장된 미결  max(gl_date) 가져오기 시작 (2011.03.23 GYJ)
			// **************************** //

			sb.setLength(0);
			ps.close();
			rs.close();

			sb.append("	select        												\n");
			//sb.append("   /*+ opt_param('_optimizer_cost_based_transformation','off') */   \n")		;	//150109 GYJ 11g 속도개선 힌트추
			//sb.append("   /* opt_param('_optimizer_cost_based_transformation','off') */   \n")		;	//220104 GYJ 속도개선 힌트추가
			sb.append("   /*+ opt_param('_optimizer_join_factorization','FALSE') */ \n");					//220522 GYJ 속도개선 힌트추가 (19c)   
			sb.append("		  max(b.gl_date)	gl_date									\n");
			sb.append("	  from (select /*+ leading(a) no_merge(b) use_nl(b) push_pred(b) no_merge(b.aida_mc) push_pred(b.aida_mc) no_merge(b.vnd) push_pred(b.vnd) no_merge(b.PENDING) push_pred(b.PENDING) no_merge(b.AI_USD) push_pred(b.AI_USD) */ \n");   //230105 GYJ 속도개선 힌트추가 (fcm통계 full로 돌려서 플랜틀어짐.)   
			sb.append("                b.gl_date									\n");
			//sb.append("	  from (select TO_DATE(SUBSTR(a.stl_erp_slip_no,2,8),'YYYY-MM-DD') as GL_DATE									\n");
			
			sb.append("			  from otc_sa_detail a, ear_if_invoice_balance_v b	\n");
			sb.append("			 where to_char(a.stl_erp_slip_no) = invoice_number	\n");
			
			//sb.append("			  from otc_sa_detail a	\n");
			sb.append("			   and a.sa_no = "+saNo+"							\n");
			sb.append("			   and a.stl_erp_slip_no is not null				\n");
			//sb.append("             AND SUBSTR(a.Stl_Erp_Slip_No, 1,1)='I' -- AP  Invoice 만. (receipt, TRX 제외 )       \n");
			sb.append("																\n");
			sb.append("			union all											\n");
			sb.append("																\n");
			sb.append("			select v.gl_Date									\n");
			sb.append("			  from otc_sa_detail a, ear_if_trx_balance_v v		\n");
			sb.append("			 where to_char(a.stl_erp_slip_no) = TRX_NUMBER		\n");
			sb.append("			   and a.sa_no = "+saNo+"							\n");
			sb.append("			   and a.stl_erp_slip_no is not null				\n");
			sb.append("																\n");
			sb.append("			union all											\n");
			sb.append("																\n");
			sb.append("			select v.gl_date									\n");
			sb.append("			  from otc_sa_detail a, ear_if_receipt_balance_v v	\n");
			sb.append("			 where to_char(a.stl_erp_slip_no) = RECEIPT_NUMBER	\n");
			sb.append("			   and a.sa_no = "+saNo+"							\n");
			sb.append("			   and a.stl_erp_slip_no is not null) b  			\n");

			log.debug("저장된 선수금 gl_date 가져오기"+ sb.toString());

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaHeadDTO settleGlDateDTO = null;
			Collection settle = new ArrayList();

			while(rs.next()){
				settleGlDateDTO = new OTCSaHeadDTO();

				settleGlDateDTO.setPosting_date(rs.getTimestamp(("gl_date").toString()));

				settle.add(settleGlDateDTO);

			}

			result.add(settle);

			//	**************************** 저장된 미결  max(gl_date) 가져오기 종료
			// **************************** //

//			 **************************** Out of Hire 가져오기 시작 111115 GYJ
			// **************************** //
			sb.setLength(0);
			ps.close();
			rs.close();

			sb.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			//sb.append(" AND  V.trsact_code IN ('A004', 'A005') ORDER BY V.GROUP_SEQ , V.TRSACT_CODE ");
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
			sb.append(" AND  V.trsact_code IN ('A004', 'A005', 'A008', 'A009') ORDER BY V.GROUP_SEQ , V.TRSACT_CODE ");

			log.debug(sb.toString());

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			//OTCSaOutHireDTO outHireDTO = null;
			OTCSaOnHireDTO outHireDTO = null;
			Collection outhires = new ArrayList();

			int row1 = 0;
			long group_seq1 = 0;
			long pre_group_seq1 = 0;
			while (rs.next()) {
				// TRSACT_CODE인 A004와 A005는 한쌍으로 존재 할때만 collection(hires)에 담는다.

				group_seq1 = rs.getLong("GROUP_SEQ");
				if (row1 == 0) {
					//outHireDTO = new OTCSaOutHireDTO();
					outHireDTO = new OTCSaOnHireDTO();
					pre_group_seq1 = group_seq1;
				} else if (group_seq1 != pre_group_seq1) {
					outhires.add(outHireDTO);
					//outHireDTO = new OTCSaOutHireDTO();
					outHireDTO = new OTCSaOnHireDTO();
				}

				//if ("A004".equals(rs.getString("TRSACT_CODE"))) {
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				if ("A004".equals(rs.getString("TRSACT_CODE")) || "A008".equals(rs.getString("TRSACT_CODE"))) {
					outHireDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					outHireDTO.setFrom_date(rs.getTimestamp("FROM_DATE"));
					outHireDTO.setTo_date(rs.getTimestamp("TO_DATE"));
					outHireDTO.setDur(new Double(rs.getDouble("SA_RATE_DUR")));
					outHireDTO.setDay_hire(new Double(rs.getDouble("SA_RATE")));
					outHireDTO.setAmount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					outHireDTO.setAmount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					outHireDTO.setBrok_reserve_flag(rs.getString("BROK_RESERV_FLAG"));		//111128 GYJ
					outHireDTO.setRemark(rs.getString("REMARK"));		//111128 GYJ

					// bbc 처리 관련 vat 정보 setting hjkang 20090813
					outHireDTO.setVat_flag(rs.getString("VAT_FLAG"));
					outHireDTO.setTax_code(rs.getString("TAX_CODE_FLAG"));
					outHireDTO.setOrg_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					outHireDTO.setTax_code_name(rs.getString("TAX_CODE_NAME"));
					outHireDTO.setVat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					outHireDTO.setVat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
				}

				//if ("A005".equals(rs.getString("TRSACT_CODE"))) {
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				if ("A005".equals(rs.getString("TRSACT_CODE")) || "A009".equals(rs.getString("TRSACT_CODE"))) {
					outHireDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					outHireDTO.setAdd_comm(new Double(rs.getDouble("SA_RATE")));
					outHireDTO.setAdd_comm_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					outHireDTO.setAdd_comm_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));

				}

				row1 = row1 + 1;
				pre_group_seq1 = group_seq1;

			}
			if (row1 > 0) {
				outhires.add(outHireDTO);
			}

			result.add(outhires);
			// **************************** Out of Hire  가져오기 종료
			// **************************** //



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public Collection saBbcLngDetailInquiryt(Long saNo, Connection conn) throws STXException, Exception {

		Collection result = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			// **************************** OnHire 가져오기 시작
			// **************************** //
			StringBuffer sb = new StringBuffer();

			sb.append(" SELECT V.*   ");
			sb.append(" FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb.append(" ORDER BY V.TRSACT_CODE ");



			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaDetailDTO detailDTO = null;

			while (rs.next()) {

				detailDTO = new OTCSaDetailDTO();

				detailDTO.setSa_no(new Long(rs.getLong("SA_NO")));
				detailDTO.setSa_seq(new Long(rs.getLong("SA_SEQ")));
				detailDTO.setTrsact_code(rs.getString("TRSACT_CODE"));
				detailDTO.setFrom_date(rs.getTimestamp("FROM_DATE"));
				detailDTO.setTo_date(rs.getTimestamp("TO_DATE"));
				detailDTO.setSa_rate(new Double(rs.getDouble("SA_RATE")));
				detailDTO.setSa_rate_dur(new Double(rs.getDouble("SA_RATE_DUR")));
				detailDTO.setLoc_sa_amt(new Double(rs.getDouble("LOC_SA_AMT")));
				detailDTO.setKrw_sa_amt(new Double(rs.getDouble("KRW_SA_AMT")));
				detailDTO.setVat_flag(rs.getString("VAT_FLAG"));
				detailDTO.setTax_code_flag(rs.getString("TAX_CODE_FLAG"));
				detailDTO.setLoc_vat_sa_amt(new Double(rs.getDouble("LOC_VAT_SA_AMT")));
				detailDTO.setKrw_vat_sa_amt(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
				detailDTO.setRemark(rs.getString("REMARK"));
				detailDTO.setCourt_flag(rs.getString("COURT_FLAG"));					 // 법원허가번호 추가 (hijang 20140313 )
				detailDTO.setCourt_admit_no(rs.getString("COURT_ADMIT_NO")); // 법원허가번호 추가 (hijang 20140313 )


				result.add(detailDTO);
			}

		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
		return result;
	}

	public String saBbcLngSave(OTCSaHeadDTO otcSaHeadDTO, Collection otcSaDetailDTOs, UserBean userBean, Connection conn)  throws Exception, STXException {

		Long sa_no = null;
		String sa_remark = "";
		String vatYN = "N";
		long group_seq = 0;

		double locToUs = 0, usToKr = 0;
		double tot_loc_sa_amt = 0,  tot_usd_sa_amt = 0,  tot_krw_sa_amt = 0;
		double tot_loc_vat_sa_amt = 0,  tot_usd_vat_sa_amt = 0,  tot_krw_vat_sa_amt = 0;
		boolean cntr_notice_m = false;
		boolean cntr_notice = false;
		boolean newFlag = false;

		String result = "";
		String srcSlipNo = "";

		try {

			log.debug("saBbcLngSave Start");

			ErpFunction erpFunc = new ErpFunction();
			StatementAccount saFunc = new StatementAccount();
			OTCSAHeadDAO headDao = new OTCSAHeadDAO();
			OTCSADetailDAO detailDao = new OTCSADetailDAO();
			OTCSaHeadVO otcSaHeadVo = new OTCSaHeadVO();
			ELTTaxXmlDAO eDao = new ELTTaxXmlDAO();	//120522 GYJ

			DbWrap dbWrap = new DbWrap();

			// hijang 추가 (20150108)
			int gl_date = Integer.parseInt(DateUtil.getTimeStamp2yyyyMMdd(otcSaHeadDTO.getPosting_date())) ;

			if (otcSaHeadDTO != null) {

				if (otcSaHeadDTO.getSa_no() == null) {
					sa_no = new Long(PKGenerator.getSequence(conn, "OTC_SA_HEAD_S"));
					srcSlipNo = erpFunc.getSourceSlipNo(otcSaHeadDTO.getPosting_date(), "OTCE", conn);   /* RYU TO-DO 2010.09.01 Open시 부터 사용 */
					newFlag = true;
				} else {
					sa_no = otcSaHeadDTO.getSa_no();
					srcSlipNo = otcSaHeadDTO.getSource_slip_no();

					if(DateUtil.getTimeStamp2yyyyMMdd(otcSaHeadDTO.getPosting_date()).equals(otcSaHeadDTO.getSource_slip_no().substring(0, 8))) {
						srcSlipNo = otcSaHeadDTO.getSource_slip_no();
					} else {
	                    srcSlipNo = erpFunc.getSourceSlipNo(otcSaHeadDTO.getPosting_date(), "OTCE", conn);   /* RYU TO-DO 2010.09.01 Open시 부터 사용 */
					}
					newFlag = false;
				}

				otcSaHeadVo.setSa_no(sa_no);
				otcSaHeadVo.setSource_slip_no(Formatter.nullTrim(srcSlipNo));
				otcSaHeadVo.setVsl_code(otcSaHeadDTO.getVsl_code());
				otcSaHeadVo.setVoy_no(otcSaHeadDTO.getVoy_no());
				otcSaHeadVo.setCht_in_out_code(otcSaHeadDTO.getCht_in_out_code());
				otcSaHeadVo.setStep_no(otcSaHeadDTO.getStep_no());
				otcSaHeadVo.setPosting_date(otcSaHeadDTO.getPosting_date());
				otcSaHeadVo.setCntr_no(otcSaHeadDTO.getCntr_no());
				otcSaHeadVo.setCntr_team_code(otcSaHeadDTO.getCntr_team_code());
				otcSaHeadVo.setCntr_acc_code(otcSaHeadDTO.getCntr_acc_code());
				otcSaHeadVo.setOp_team_code(otcSaHeadDTO.getOp_team_code());
				otcSaHeadVo.setSemi_final_flag(otcSaHeadDTO.getSemi_final_flag());
				otcSaHeadVo.setCurcy_code(otcSaHeadDTO.getCurcy_code());
				otcSaHeadVo.setExc_date(otcSaHeadDTO.getExc_date());
				otcSaHeadVo.setExc_rate_type(otcSaHeadDTO.getExc_rate_type());
				otcSaHeadVo.setLoc_exc_rate(otcSaHeadDTO.getLoc_exc_rate());


				if ("USD".equals(otcSaHeadDTO.getCurcy_code())) {
					usToKr = otcSaHeadDTO.getLoc_exc_rate().doubleValue();
					locToUs = 1;
				} else {
					usToKr = erpFunc.exchangeRateSearch("USD", "KRW", DateUtil.getTimeStamp2yyyyMMdd(otcSaHeadDTO.getExc_date()), otcSaHeadDTO.getExc_rate_type(), conn);
					locToUs = erpFunc.exchangeRateSearch(otcSaHeadDTO.getCurcy_code(), "USD", DateUtil.getTimeStamp2yyyyMMdd(otcSaHeadDTO.getExc_date()), otcSaHeadDTO.getExc_rate_type(), conn);
				}
				otcSaHeadVo.setUsd_exc_rate(new Double(usToKr));
				otcSaHeadVo.setUsd_loc_rate(new Double(locToUs));
				otcSaHeadVo.setCbVoy_no(otcSaHeadDTO.getCbVoy_no());
				otcSaHeadVo.setInt_txn_reconc_key(otcSaHeadDTO.getInt_txn_reconc_key());
				otcSaHeadVo.setWth_flag(otcSaHeadDTO.getWth_flag());
				otcSaHeadVo.setWth_nat_code(Formatter.nullTrim(otcSaHeadDTO.getWth_nat_code()));
				otcSaHeadVo.setWth_hire_bal_amt(new Double(Formatter.nullDouble(otcSaHeadDTO.getWth_hire_bal_amt())));
				otcSaHeadVo.setWth_hire_bal_reduce_rate(new Double(Formatter.nullDouble(otcSaHeadDTO.getWth_hire_bal_reduce_rate())));
				otcSaHeadVo.setCp_item_no(otcSaHeadDTO.getCp_item_no());

				otcSaHeadVo.setTax_invo_flag("N");
				otcSaHeadVo.setSale_tax_flag("N");
				otcSaHeadVo.setPur_tax_flag("N");
				otcSaHeadVo.setProcess_sts_flag("U");
				otcSaHeadVo.setProcess_upd_date(new Timestamp(System.currentTimeMillis()));
				otcSaHeadVo.setCancel_flag("N");
				otcSaHeadVo.setSys_upd_date(new Timestamp(System.currentTimeMillis()));
				otcSaHeadVo.setSys_upd_user_id(userBean.getUser_id());

				if(newFlag) {
					// Head Insert
					otcSaHeadVo.setSys_cre_date(new Timestamp(System.currentTimeMillis()));
					otcSaHeadVo.setSys_cre_user_id(userBean.getUser_id());
					otcSaHeadVo.setSa_reg_user_id(userBean.getUser_id());
					headDao.saBbcLngHeadInsert(otcSaHeadVo, conn);
				} else {
					// Head update
					otcSaHeadVo.setSys_cre_date(otcSaHeadDTO.getSys_cre_date());
					otcSaHeadVo.setSys_cre_user_id(otcSaHeadDTO.getSys_cre_user_id());
					otcSaHeadVo.setSa_reg_user_id(otcSaHeadDTO.getSa_reg_user_id());
					headDao.saBbcLngHeadUpdate(otcSaHeadVo, conn);
				}
			}

			if (!newFlag){
				// Detail 내역을 지우고 새로 구성함
				detailDao.saBbcLngDetailDelete(sa_no, conn);
				// Taxl 내역을 지움
				OBNBnkSplyInvoHeadDAO dao = new OBNBnkSplyInvoHeadDAO();
				dao.ccdTaxCancelFlagUpdate(sa_no, "OTCE", conn);
				// Elt Tax Xml내역을 지움 120522 GYJ
				//eDao.PTaxHoldFlagDelete(sa_no.toString(), userBean,conn);
				eDao.PTaxXmlHdDeleteForSA(sa_no.toString(),  conn);

			}

			//detail insert
			if (otcSaDetailDTOs != null) {

				Iterator iterator = otcSaDetailDTOs.iterator();
				OTCSaDetailVO otcSaDetailVo = null;

				int detailSize = otcSaDetailDTOs.size();
				boolean lastRow = false; // AP 생성을 위한 구분자
				Timestamp fromDate = null, toDate = null;

				CCDTrsactTypeMDAO tdao = new CCDTrsactTypeMDAO();
				String gl_acc_code = "",  evidence_flag = "", acc_nat_code = "", cht_in_out_code = "" ;

				EARCustomerVendorVDAO aAccDao = new EARCustomerVendorVDAO();
				EARCustomerVendorVVO aAccVO = aAccDao.customerSelect(conn, Formatter.nullTrim(otcSaHeadDTO.getCntr_acc_code()));
				if (aAccVO != null) {
					acc_nat_code = Formatter.nullTrim(aAccVO.getNat_code());
				}

				if (Formatter.nullTrim(otcSaHeadDTO.getCht_in_out_code()).equals("T")  ) {
					cht_in_out_code = "C";
				}	else {
					cht_in_out_code = "L";
				}

				for (int cnt = 0; cnt <= detailSize; cnt++) {
					if(cnt < detailSize) {
						otcSaDetailVo = (OTCSaDetailVO) iterator.next();

						//RYU 2010.02.26 Brokerage도 from-to 입력함
						//if("XA01".equals(otcSaDetailVo.getTrsact_code()) || "A001".equals(otcSaDetailVo.getTrsact_code())) {
						//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
						if("XA01".equals(otcSaDetailVo.getTrsact_code()) || "A001".equals(otcSaDetailVo.getTrsact_code()) || "A006".equals(otcSaDetailVo.getTrsact_code())) {
							fromDate = otcSaDetailVo.getFrom_date();
							toDate = otcSaDetailVo.getTo_date();
					    }

						//증빙 종류
						if ("KR".equals(acc_nat_code)) {
							// 일반관리비 은행수수료('630601') -> 고정비 은행수수료('540203') 으로 계정 변경 관련.... 신규 거래유형(K007) 추가 (20160906 HIJANG)
							if ("K001".equals(otcSaDetailVo.getTrsact_code()) || "K002".equals(otcSaDetailVo.getTrsact_code()) || "K003".equals(otcSaDetailVo.getTrsact_code()) || "K007".equals(otcSaDetailVo.getTrsact_code())) {
								evidence_flag = "09";
							} else {
								evidence_flag = "01";
							}

						} else {
							// 일반관리비 은행수수료('630601') -> 고정비 은행수수료('540203') 으로 계정 변경 관련.... 신규 거래유형(K007) 추가 (20160906 HIJANG)
							if ("K001".equals(otcSaDetailVo.getTrsact_code()) || "K002".equals(otcSaDetailVo.getTrsact_code()) || "K003".equals(otcSaDetailVo.getTrsact_code()) || "K007".equals(otcSaDetailVo.getTrsact_code())) {
								evidence_flag = "09";
							} else {
								evidence_flag = "06";
							}
						}

					} else {
						otcSaDetailVo = new OTCSaDetailVO();
						evidence_flag = "";
						lastRow = true;
					}
					log.debug("RYU cnt value = "+ cnt);
					if (("insert".equals(otcSaDetailVo.getStatus()) || "normal".equals(otcSaDetailVo.getStatus()) || "update".equals(otcSaDetailVo.getStatus())) || lastRow) {

						// 기 생성된 AP 내역은 반영하지 않고 새로 생성함
						if("L001".equals(otcSaDetailVo.getTrsact_code())) continue;

						otcSaDetailVo.setSa_no(sa_no);
						otcSaDetailVo.setSa_seq(detailDao.saSeqMaxNoSelect(sa_no, conn));
						otcSaDetailVo.setVsl_code(otcSaHeadDTO.getVsl_code());
						otcSaDetailVo.setVoy_no(otcSaHeadDTO.getVoy_no());
						otcSaDetailVo.setInt_txn_reconc_key(otcSaHeadDTO.getInt_txn_reconc_key());
						otcSaDetailVo.setCurcy_code(otcSaHeadDTO.getCurcy_code());
						otcSaDetailVo.setExc_date(otcSaHeadDTO.getExc_date());
						otcSaDetailVo.setExc_rate_type(otcSaHeadDTO.getExc_rate_type());
						otcSaDetailVo.setLoc_exc_rate(otcSaHeadDTO.getLoc_exc_rate());
						otcSaDetailVo.setUsd_exc_rate(new Double(usToKr));
						otcSaDetailVo.setUsd_loc_rate(new Double(locToUs));

						log.debug("Posting_date : " + gl_date );
						log.debug("getTrsact_code : " + otcSaDetailVo.getTrsact_code() );

						// 결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 )
						// : GL_DATE 에 따른,, TRSACT_CODE 바꿔치기..!! (HIJANG 20150109)
						if( "A001".equals(otcSaDetailVo.getTrsact_code()) || "A006".equals(otcSaDetailVo.getTrsact_code()) ){
							if( gl_date >=  20150201 ){
								otcSaDetailVo.setTrsact_code("A006");
							}else{
								otcSaDetailVo.setTrsact_code("A001");
							}
						}


						if(lastRow) {
							// 총 금액이 0 이면 AP 생성없
							log.debug("RYU total value = "+ (tot_loc_sa_amt + tot_loc_vat_sa_amt));
							if((tot_loc_sa_amt + tot_loc_vat_sa_amt) == 0.0) continue;

							otcSaDetailVo.setTrsact_code("L001");
							CCDTrsactTypeMVO tVO = tdao.ccdTrsactTypeMPaymentSelect("SOMO", cht_in_out_code, otcSaDetailVo.getTrsact_code(), conn);
							if (tVO != null) {
								gl_acc_code = Formatter.nullTrim(tVO.getO_gl_acc_code());   //RYU 2010.07.12
							} else {
								gl_acc_code = "";
							}

							otcSaDetailVo.setLoc_sa_amt(new Double(tot_loc_sa_amt + tot_loc_vat_sa_amt));
							otcSaDetailVo.setUsd_sa_amt(new Double(tot_usd_sa_amt+ tot_usd_vat_sa_amt));
							otcSaDetailVo.setKrw_sa_amt(new Double(tot_krw_sa_amt+ tot_krw_vat_sa_amt));
							otcSaDetailVo.setLoc_vat_sa_amt(new Double(0));
							otcSaDetailVo.setUsd_vat_sa_amt(new Double(0));
							otcSaDetailVo.setKrw_vat_sa_amt(new Double(0));
							otcSaDetailVo.setDue_date(otcSaHeadDTO.getDue_date());
							otcSaDetailVo.setPymt_term(("AP" + otcSaHeadDTO.getPymt_term()).trim());
							otcSaDetailVo.setTerms_date(otcSaHeadDTO.getTerms_date());
							otcSaDetailVo.setPymt_hold_flag(otcSaHeadDTO.getPymt_hold_flag());

							// 장기용대선 미신고 항차의 경우는 hold 처리함
							if ("T".equals(otcSaHeadDTO.getCht_in_out_code())){
								cntr_notice_m = saFunc.saCntrNoticeM(otcSaHeadDTO.getCntr_no(), conn);
							}

							if(cntr_notice_m){
								cntr_notice = saFunc.saCntrNotice(otcSaHeadDTO.getCntr_no(), conn);

								if(!cntr_notice){
									otcSaDetailVo.setPymt_hold_flag("Y");
									log.debug("cntr_notice__2 :: cntr_m :: " + cntr_notice_m +" 장기용대선 신고 대상, 신고 무 cntr_notice ::" + cntr_notice +" " +otcSaDetailVo.getPymt_hold_flag());
								}
							}

							// 법원허가번호 등록여부 체크 추가 ( 20140304 hijang )
							otcSaDetailVo.setCourt_flag(otcSaHeadDTO.getCourt_flag());
							otcSaDetailVo.setCourt_admit_no(otcSaHeadDTO.getCourt_admit_no());


							otcSaDetailVo.setPymt_meth("CHECK");
							otcSaDetailVo.setBank_acc_id(otcSaHeadDTO.getBank_acc_id());
							otcSaDetailVo.setBank_acc_desc(otcSaHeadDTO.getBank_acc_desc());

							// From, to Date 세팅 - on hire의 기간으로
							ArrayList inVar = new ArrayList();
							inVar.add(sa_no);
							inVar.add(otcSaHeadDTO.getVsl_code());
							inVar.add(otcSaHeadDTO.getVoy_no());
							inVar.add(otcSaHeadDTO.getCht_in_out_code());
							inVar.add(otcSaHeadDTO.getStep_no());
							StringBuffer sbAP = new StringBuffer();
							sbAP.append("{ call SA_AP_HIRE_PRC(?,?,?,?,?,?,?) }");
							Object basicDatas[] = dbWrap.getObjectCstmt(conn, sbAP.toString(), inVar.toArray(), 2);

							int iAP = 0;
							if (basicDatas != null) {

								String fd = (String) basicDatas[iAP++];
								String td = (String) basicDatas[iAP++];

								if( fd != null && fd.length() >= 19){
									otcSaDetailVo.setFrom_date(Timestamp.valueOf(fd));

								} else	if(fromDate != null){
									otcSaDetailVo.setFrom_date(fromDate);  // XA01 -Brokerage의 경우도 날짜 세팅해줌   //RYU 2010.02.26
								}

								if(td != null && td.length() >=19){
									otcSaDetailVo.setTo_date(Timestamp.valueOf(td));

								} else 	if( toDate != null){
									otcSaDetailVo.setTo_date(toDate);   // XA01 -Brokerage의 경우도 날짜 세팅해줌   //RYU 2010.02.26
								}

							} else {  // XA01 -Brokerage의 경우도 날짜 세팅해줌   //RYU 2010.02.26

								if( fromDate != null){
									otcSaDetailVo.setFrom_date(fromDate);
								}

								if( toDate != null){
									otcSaDetailVo.setTo_date(toDate);
								}

							}

							otcSaDetailVo.setSa_rate(new Double(0));
							otcSaDetailVo.setSa_rate_dur(new Double(0));

							String cht_in_out_name = "";
							if (Formatter.nullTrim(otcSaHeadDTO.getCht_in_out_code()).equals("T")  ) {
								cht_in_out_name = "IN";
							}	else {
								cht_in_out_name = "OUT";
							}

							sa_remark = Formatter.nullTrim(otcSaHeadDTO.getVsl_code())
											.concat(" ").concat(otcSaHeadDTO.getVoy_no().toString())
											.concat(" ").concat(cht_in_out_name).concat(" ")
											.concat(otcSaHeadDTO.getStep_no().toString()).concat("STEP");

							otcSaDetailVo.setRemark(sa_remark);
							otcSaDetailVo.setVat_flag("N");
							otcSaDetailVo.setTax_rate(new Double(0));

						} else { // lastRow가 아니면

							CCDTrsactTypeMVO tVO = tdao.ccdTrsactTypeMPaymentSelect("SOMO", cht_in_out_code, otcSaDetailVo.getTrsact_code(), conn);
							if (tVO != null) {
								gl_acc_code = Formatter.nullTrim(tVO.getO_gl_acc_code());   //RYU 2010.07.12
							} else {
								gl_acc_code = "";
							}

							otcSaDetailVo.setDue_date(otcSaHeadDTO.getPosting_date());
							otcSaDetailVo.setTerms_date(otcSaHeadDTO.getPosting_date());
							otcSaDetailVo.setPymt_hold_flag("N");
							otcSaDetailVo.setPymt_meth("CLEARING");

							otcSaDetailVo.setTrsact_code(otcSaDetailVo.getTrsact_code());
							otcSaDetailVo.setUsd_sa_amt(new Double(Formatter.round(otcSaDetailVo.getLoc_sa_amt().doubleValue() * locToUs, 2)));

							// 법원허가번호 등록여부 체크 추가 ( 20140304 hijang )
							otcSaDetailVo.setCourt_flag("");
							otcSaDetailVo.setCourt_admit_no("");


							if(otcSaDetailVo.getVat_flag() == null || "0".equals(otcSaDetailVo.getVat_flag())) {
								otcSaDetailVo.setVat_flag("N") ;
							} else {
								otcSaDetailVo.setVat_flag("Y") ;
							}

							if ("1".equals(otcSaDetailVo.getTax_code_flag())) {
								otcSaDetailVo.setTax_code_id("VP111101");
								otcSaDetailVo.setTax_rate(new Double(0.1));
								otcSaDetailVo.setUsd_vat_sa_amt(new Double(Formatter.round(otcSaDetailVo.getLoc_vat_sa_amt().doubleValue() * locToUs, 2)));
								vatYN = "Y";

							} else if ("2".equals(otcSaDetailVo.getTax_code_flag())) {
								otcSaDetailVo.setTax_code_id("VP121101");
								otcSaDetailVo.setTax_rate(new Double(0));
								otcSaDetailVo.setLoc_vat_sa_amt(new Double(0));
								otcSaDetailVo.setUsd_vat_sa_amt(new Double(0));
								otcSaDetailVo.setKrw_vat_sa_amt(new Double(0));
								vatYN = "Y";

							} else {
								otcSaDetailVo.setTax_rate(new Double(0));
								otcSaDetailVo.setLoc_vat_sa_amt(new Double(0));
								otcSaDetailVo.setUsd_vat_sa_amt(new Double(0));
								otcSaDetailVo.setKrw_vat_sa_amt(new Double(0));
							}

							//if("A001".equals(otcSaDetailVo.getTrsact_code())) {
							//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
							if("A001".equals(otcSaDetailVo.getTrsact_code()) || "A006".equals(otcSaDetailVo.getTrsact_code())) {
								++group_seq;
								otcSaDetailVo.setGroup_seq(new Long(group_seq));
							}
						}  // end of lastRow관련

						otcSaDetailVo.setSa_rate(new Double(Formatter.nullDouble(otcSaDetailVo.getSa_rate())));
						otcSaDetailVo.setFactor(new Double(0));
						otcSaDetailVo.setBnk_qty(new Double(0));
						otcSaDetailVo.setBnk_prc(new Double(0));

						otcSaDetailVo.setStl_vsl_code(otcSaHeadDTO.getVsl_code());
						otcSaDetailVo.setStl_voy_no(otcSaHeadDTO.getVoy_no());
						otcSaDetailVo.setStl_cntr_acc_code(otcSaHeadDTO.getCntr_acc_code());

						otcSaDetailVo.setSys_cre_date(new Timestamp(System.currentTimeMillis()));
						otcSaDetailVo.setSys_cre_user_id(userBean.getUser_id());
						otcSaDetailVo.setSys_upd_date(new Timestamp(System.currentTimeMillis()));
						otcSaDetailVo.setSys_upd_user_id(userBean.getUser_id());

						otcSaDetailVo.setGl_acc_code(gl_acc_code);
						otcSaDetailVo.setEvidence_flag(evidence_flag);

						// 원천징수는 AP에 반영안됨
						if(!lastRow && !"M001".equals(otcSaDetailVo.getTrsact_code()) && !"M002".equals(otcSaDetailVo.getTrsact_code()) && !"M004".equals(otcSaDetailVo.getTrsact_code())) {
							tot_loc_sa_amt = Formatter.round(tot_loc_sa_amt, 2) + Formatter.round(otcSaDetailVo.getLoc_sa_amt().doubleValue(), 2);
							tot_usd_sa_amt = Formatter.round(tot_usd_sa_amt, 2) + Formatter.round(otcSaDetailVo.getUsd_sa_amt().doubleValue(), 2);
							tot_krw_sa_amt = Formatter.roundFloor(tot_krw_sa_amt, 0) + Formatter.roundFloor(otcSaDetailVo.getKrw_sa_amt().longValue(), 0);

							tot_loc_vat_sa_amt = Formatter.round(tot_loc_vat_sa_amt, 2) + Formatter.round(otcSaDetailVo.getLoc_vat_sa_amt().doubleValue(), 2);
							tot_usd_vat_sa_amt = Formatter.round(tot_usd_vat_sa_amt, 2) + Formatter.round(otcSaDetailVo.getUsd_vat_sa_amt().doubleValue(), 2);
							tot_krw_vat_sa_amt = Formatter.roundFloor(tot_krw_vat_sa_amt, 0) + Formatter.roundFloor(otcSaDetailVo.getKrw_vat_sa_amt().longValue(), 0);
						}
                       // Detail 저장
						detailDao.saDetailInsert(otcSaDetailVo, userBean, conn);
					} // end of status(insert, update, normal)
				} //end of for


				//--------- 법원허가번호 체크(시작) -----------//
				/*CommonDao commDao = new CommonDao();
				String msg = "";
				String ret_value = "";

				/// payment hold = 'Y' 일때는
				// '법원/포괄허가번호 등록여부 체크' 및 '법원/포괄허가번호 한도금액 체크' 를 할  필요없다 ( hijang 20140502 )
				if( otcSaHeadDTO.getPymt_hold_flag() != null && !"Y".equals(otcSaHeadDTO.getPymt_hold_flag()) ){
					// 1) 법원/포괄허가번호 등록여부 체크
					ret_value = commDao.courtAdmitNoCheck(String.valueOf(sa_no), "SOA", conn) ;
					if(!"N".equals(ret_value)){	//5000만원 이상인데 법원허가 번호 없는 경우
						msg = "송금액이 5천만 원 이상일 경우 반드시 해당 송금에 대한 \n법원/포괄허가번호를 지정해야 합니다.\n법원/포괄허가가 불가피하게 늦어지는 경우에는 임시번호를 생성하여\n지정해야 합니다.\n";
						throw new STXException(msg);
					}

					// 2) 법원/포괄허가번호 한도금액 체크
					ret_value = commDao.courtAdmitBudgetLimitCheck(String.valueOf(sa_no), "SOA", conn) ;
					if( "FAIL".equals(ret_value.substring(0,4)) ){
						msg = "입력 금액이 집행 가능 잔여한도("+ret_value.substring(4)+"원)를 초과하여, \n결재 요청을 할 수 없습니다.\n한도 초과액 송금을 위해서는 추가적인 법원/포괄허가가 필요합니다.\n환율 차이 등으로 불가피하게 잔여한도를 초과하는 경우에는\nRM팀 담당자와 협의하시기 바랍니다(담당자: 조성형 차장 5236).\n";
						throw new STXException(msg);
					}
				}*/ //150730 GYJ 법정관리 종료
				//------- 법원허가번호 체크(종료) -----------//

			}

			// 원천징수 예수재세 금액을 소득세, 주민세의 합으로 다시 한번 update 함
			detailDao.saWthTaxAmtUpdate(sa_no, conn);

			otcSaHeadVo.setLoc_tot_sa_amt(new Double(tot_loc_sa_amt));
			otcSaHeadVo.setUsd_tot_sa_amt(new Double(tot_usd_sa_amt));
			otcSaHeadVo.setKrw_tot_sa_amt(new Double(tot_krw_sa_amt));

			otcSaHeadVo.setLoc_tot_vat_sa_amt(new Double(tot_loc_vat_sa_amt));
			otcSaHeadVo.setUsd_tot_vat_sa_amt(new Double(tot_usd_vat_sa_amt));
			otcSaHeadVo.setKrw_tot_vat_sa_amt(new Double(tot_krw_vat_sa_amt));

			otcSaHeadVo.setLoc_tot_bal_amt(new Double(tot_loc_sa_amt + tot_loc_vat_sa_amt));
			otcSaHeadVo.setUsd_tot_bal_amt(new Double(tot_usd_sa_amt + tot_usd_vat_sa_amt));
			otcSaHeadVo.setKrw_tot_bal_amt(new Double(tot_krw_sa_amt + tot_krw_vat_sa_amt));


			if("Y".equals(vatYN)){
				otcSaHeadVo.setPur_tax_flag("Y");
			}

			headDao.saBbcLngHeadUpdate(otcSaHeadVo, conn);

			//result = headDao.saMainInfoSelect((OTCSaHeadDTO)otcSaHeadVo, conn);

			result = "SUC-0100";

		} catch (Exception e) {

			log.error(e.getMessage());
			e.printStackTrace();
			throw new STXException(e);
		}

		return result;

	}


	public String saWthTaxAmtUpdate(Long saNo, Connection conn) throws Exception, STXException {

		String result = "";
		PreparedStatement ps = null;
		try {

			if (saNo != null) {

				StringBuffer sb = new StringBuffer();
				// Query 가져오기


				sb.append("\n   UPDATE OTC_SA_DETAIL A							   ");
				sb.append("\n   SET (A.USD_SA_AMT, A.LOC_SA_AMT, A.KRW_SA_AMT) = 			   ");
				sb.append("\n       (									   ");
				sb.append("\n       SELECT SUM(A.USD_SA_AMT), SUM(A.LOC_SA_AMT), SUM(A.KRW_SA_AMT)	   ");
				sb.append("\n       FROM OTC_SA_DETAIL A						   ");
				sb.append("\n       WHERE A.SA_NO = ?						   ");
				sb.append("\n       AND A.TRSACT_CODE IN ('M001', 'M002')				   ");
				sb.append("\n       )									   ");
				sb.append("\n   WHERE A.SA_NO = ?						   ");
				sb.append("\n   AND A.TRSACT_CODE = 'M004'						   ");

				ps = conn.prepareStatement(sb.toString());

				int i = 1;
				ps.setLong(i++, Formatter.nullLong(saNo));
				ps.setLong(i++, Formatter.nullLong(saNo));

				ps.executeUpdate();

				result = "SUC-0600";

			}

		} catch (Exception e) {

			throw new STXException(e);
		} finally {
			try {

				if (ps != null)
					ps.close();
			} catch (SQLException e1) {

				throw new STXException(e1);
			}
		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saOffHireSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public Collection saOffHireSelect(Long saNo, Connection conn) throws STXException, Exception {

		Collection result = null;


		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {

			// **************************** NegoAmount/Compensation 가져오기 시작
			// **************************** //
			StringBuffer sb = new StringBuffer();

			sb.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			//sb.append(" AND  V.trsact_code  = 'H005' ORDER BY V.SA_SEQ ");
			sb.append(" AND  V.trsact_code  IN ( 'H005','H011' ) ORDER BY V.SA_SEQ ");	//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150204



			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaOffHireNegoDTO negoDTO = new OTCSaOffHireNegoDTO();
			result = new ArrayList();

			while (rs.next()) {
				negoDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
				negoDTO.setAmount_usd(new Double(rs.getDouble("USD_SA_AMT")));
				negoDTO.setAmount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
				negoDTO.setVat_flag(rs.getString("VAT_FLAG"));
				negoDTO.setTax_code(rs.getString("TAX_CODE_FLAG"));
				negoDTO.setOrg_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
				negoDTO.setTax_code_name(rs.getString("TAX_CODE_NAME"));
				negoDTO.setVat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
				negoDTO.setVat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
				//채산항차 추가 170523 GYJ
				negoDTO.setVoyage(new Long(rs.getLong("VOY_NO")));
				//remark 추가 221025 GYJ
				negoDTO.setRemark(rs.getString("REMARK"));
			}

			result.add(negoDTO);
			// **************************** NegoAmount/Compensation 가져오기 종료
			// **************************** //

			// **************************** Off Hire 가져오기 시작
			// **************************** //
			StringBuffer sb1 = new StringBuffer();

			sb1.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb1.append("          FROM OTC_SA_DETAIL V       ");
			sb1.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			//sb1.append(" AND  V.trsact_code IN ('H001','H002','H003','H004','H006','H007','H008')  ORDER BY V.GROUP_SEQ,V.TRSACT_CODE ");
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
			sb1.append(" AND  V.trsact_code IN ('H001','H002','H009','H010', 'H003','H004','H006','H007','H008')  ORDER BY V.GROUP_SEQ,V.TRSACT_CODE ");



			ps1 = conn.prepareStatement(sb1.toString());
			rs1 = ps1.executeQuery();

			OTCSaOffHireDTO offDTO = null;
			Collection offs = new ArrayList();

			int row = 0;
			long group_seq = 0;
			long pre_group_seq = 0;
			while (rs1.next()) {

				group_seq = rs1.getLong("GROUP_SEQ");
				if (row == 0) {
					offDTO = new OTCSaOffHireDTO();
					pre_group_seq = group_seq;
				} else if (group_seq != pre_group_seq) {
					offs.add(offDTO);
					offDTO = new OTCSaOffHireDTO();
				}

				//if ("H001".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE"))) || "H006".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE")))) {
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				if ("H001".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE")))
						|| "H009".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE")))
						|| "H006".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE")))) {

					offDTO.setSa_no(new Double(rs1.getDouble("SA_NO")));
					offDTO.setFrom_date(rs1.getTimestamp("FROM_DATE"));
					offDTO.setTo_date(rs1.getTimestamp("TO_DATE"));
					offDTO.setDuration(new Double(rs1.getDouble("SA_RATE_DUR")));
					offDTO.setDay_hire(new Double(rs1.getDouble("SA_RATE")));
					offDTO.setFactor(new Double(rs1.getDouble("FACTOR")));
					offDTO.setAmount_usd(new Double(rs1.getDouble("USD_SA_AMT")));
					offDTO.setAmount_krw(new Double(rs1.getDouble("KRW_SA_AMT")));
					offDTO.setRemark(rs1.getString("REMARK"));
					offDTO.setSupplement(rs1.getString("SUPPLEMENT"));
					offDTO.setStl_flag(Formatter.nullTrim(rs1.getString("STL_FLAG")));
					offDTO.setOwn_spd_clm_flag(Formatter.nullTrim(rs1.getString("OWN_SPD_CLM_FLAG")));

					offDTO.setVat_flag(rs1.getString("VAT_FLAG"));
					offDTO.setTax_code(rs1.getString("TAX_CODE_FLAG"));
					offDTO.setOrg_vat_no(new Long(rs1.getLong("ORG_VAT_NO")));
					offDTO.setTax_code_name(rs1.getString("TAX_CODE_NAME"));
					offDTO.setVat_krw(new Double(rs1.getDouble("KRW_VAT_SA_AMT")));
					offDTO.setVat_usd(new Double(rs1.getDouble("USD_VAT_SA_AMT")));

				//} else if ("H002".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE")))) {
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				} else if ("H002".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE"))) || "H010".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE")))) {

					offDTO.setSa_no(new Double(rs1.getDouble("SA_NO")));
					offDTO.setAdd_comm(new Double(rs1.getDouble("SA_RATE")));
					offDTO.setAdd_comm_amount_krw(new Double(rs1.getDouble("KRW_SA_AMT")));
					offDTO.setAdd_comm_amount_usd(new Double(rs1.getDouble("USD_SA_AMT")));
					offDTO.setStl_flag(Formatter.nullTrim(rs1.getString("STL_FLAG")));
					offDTO.setOwn_spd_clm_flag(Formatter.nullTrim(rs1.getString("OWN_SPD_CLM_FLAG")));

				} else if ("H003".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE"))) || "H007".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE")))) {

					offDTO.setSa_no(new Double(rs1.getDouble("SA_NO")));
					offDTO.setFo_qty(new Double(rs1.getDouble("BNK_QTY")));
					offDTO.setFo_price(new Double(rs1.getDouble("BNK_PRC")));
					offDTO.setFo_amount_krw(new Double(rs1.getDouble("KRW_SA_AMT")));
					offDTO.setFo_amount_usd(new Double(rs1.getDouble("USD_SA_AMT")));
					offDTO.setFo_vat_flag(rs1.getString("VAT_FLAG"));
					offDTO.setFo_tax_code(rs1.getString("TAX_CODE_FLAG"));
					offDTO.setFo_org_vat_no(new Long(rs1.getLong("ORG_VAT_NO")));
					offDTO.setFo_vat_amount_krw(new Double(rs1.getDouble("KRW_VAT_SA_AMT")));
					offDTO.setFo_vat_amount_usd(new Double(rs1.getDouble("USD_VAT_SA_AMT")));
					offDTO.setFo_tax_code_name(rs1.getString("TAX_CODE_NAME"));
					offDTO.setStl_flag(Formatter.nullTrim(rs1.getString("STL_FLAG")));
					offDTO.setOwn_spd_clm_flag(Formatter.nullTrim(rs1.getString("OWN_SPD_CLM_FLAG")));

				} else if ("H004".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE"))) || "H008".equals(Formatter.nullTrim(rs1.getString("TRSACT_CODE")))) {

					offDTO.setSa_no(new Double(rs1.getDouble("SA_NO")));
					offDTO.setDo_qty(new Double(rs1.getDouble("BNK_QTY")));
					offDTO.setDo_price(new Double(rs1.getDouble("BNK_PRC")));
					offDTO.setDo_amount_krw(new Double(rs1.getDouble("KRW_SA_AMT")));
					offDTO.setDo_amount_usd(new Double(rs1.getDouble("USD_SA_AMT")));
					offDTO.setDo_vat_flag(rs1.getString("VAT_FLAG"));
					offDTO.setDo_tax_code(rs1.getString("TAX_CODE_FLAG"));
					offDTO.setDo_org_vat_no(new Long(rs1.getLong("ORG_VAT_NO")));
					offDTO.setDo_vat_amount_krw(new Double(rs1.getDouble("KRW_VAT_SA_AMT")));
					offDTO.setDo_vat_amount_usd(new Double(rs1.getDouble("USD_VAT_SA_AMT")));
					offDTO.setDo_tax_code_name(rs1.getString("TAX_CODE_NAME"));
					offDTO.setStl_flag(Formatter.nullTrim(rs1.getString("STL_FLAG")));
					offDTO.setOwn_spd_clm_flag(Formatter.nullTrim(rs1.getString("OWN_SPD_CLM_FLAG")));

				}

				row = row + 1;
				pre_group_seq = group_seq;

			} // while
			if (row > 0) {
				offs.add(offDTO);
			}
			result.add(offs);
			// **************************** Off Hire 가져오기 종료
			// **************************** //


		} catch (Exception e) {

			throw new STXException(e);
		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public Collection saHcleanSearch(Long saNo, Connection conn) throws STXException, Exception {

		Collection result = null;

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		try {
			// **************************** Hold Cleaning 가져오기 시작
			// **************************** //
			StringBuffer sb = new StringBuffer();

			String dSql = " SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*  FROM OTC_SA_DETAIL V   ";
			sb.append(dSql);
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			//sb.append(" AND  V.trsact_code =  'F001' ORDER BY V.SA_SEQ ");
			sb.append(" AND  V.trsact_code IN ( 'F001', 'F003' ) ORDER BY V.SA_SEQ ");	//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150204



			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			result = new ArrayList();
			OTCSaHCleanDTO hcleanDTO = null;
			while (rs.next()) {
				hcleanDTO = new OTCSaHCleanDTO();
				hcleanDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
				hcleanDTO.setAmount_usd(new Double(rs.getDouble("USD_SA_AMT")));
				hcleanDTO.setAmount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
				hcleanDTO.setRemark(rs.getString("REMARK"));
				hcleanDTO.setVoyage(new Long(rs.getLong("VOY_NO")));
				hcleanDTO.setOrg_vat_no(new Long(rs.getLong("ORG_VAT_NO")));

			}

			result.add(hcleanDTO);
			// **************************** Hold Cleaning 가져오기 종료
			// **************************** //

			// **************************** Intermediate Hold Cleaning 가져오기 시작
			// **************************** //s
			StringBuffer sb1 = new StringBuffer();
			sb1.append(dSql);
			sb1.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			//sb1.append(" AND  V.trsact_code ='F002'  ORDER BY V.SA_SEQ ");
			sb1.append(" AND  V.trsact_code IN ('F002','F004')  ORDER BY V.SA_SEQ ");	//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150204



			ps1 = conn.prepareStatement(sb1.toString());
			rs1 = ps1.executeQuery();

			OTCSaInterHCleanDTO interDTO = null;
			Collection inters = new ArrayList();
			while (rs1.next()) {
				interDTO = new OTCSaInterHCleanDTO();
				interDTO.setSa_no(new Double(rs1.getDouble("SA_NO")));
				interDTO.setAmount_krw(new Double(rs1.getDouble("KRW_SA_AMT")));
				interDTO.setAmount_usd(new Double(rs1.getDouble("USD_SA_AMT")));
				interDTO.setVessel(rs1.getString("VSL_CODE"));
				interDTO.setVoyage(new Long(rs1.getLong("VOY_NO")));
				interDTO.setOrg_vat_no(new Long(rs1.getLong("ORG_VAT_NO")));
				interDTO.setRemark(rs1.getString("REMARK"));
				inters.add(interDTO);
			}
			result.add(inters);
			// **************************** Intermediate Hold Cleaning 가져오기 종료
			// **************************** //

			// **************************** Ballast Bonus 가져오기 시작
			// **************************** //
			StringBuffer sb2 = new StringBuffer();
			sb2.append(dSql);
			sb2.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			//sb2.append(" AND  V.trsact_code IN ('G001','G002')  ORDER BY V.SA_SEQ ");
			sb2.append(" AND  V.trsact_code IN ('G001','G003','G002','G004')  ORDER BY V.SA_SEQ ");	//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150204



			ps2 = conn.prepareStatement(sb2.toString());
			rs2 = ps2.executeQuery();

			OTCSaBallstBonusDTO ballstDTO = new OTCSaBallstBonusDTO();
			while (rs2.next()) {
				ballstDTO.setSa_no(new Double(rs2.getDouble("SA_NO")));
				//if ("G001".equals(Formatter.nullTrim(rs2.getString("TRSACT_CODE")))) {
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150204
				if ("G001".equals(Formatter.nullTrim(rs2.getString("TRSACT_CODE"))) || "G003".equals(Formatter.nullTrim(rs2.getString("TRSACT_CODE")))) {
					ballstDTO.setAmount_krw(new Double(rs2.getDouble("KRW_SA_AMT")));
					ballstDTO.setAmount_usd(new Double(rs2.getDouble("USD_SA_AMT")));
					ballstDTO.setRemark(rs2.getString("REMARK"));

					ballstDTO.setVat_flag(rs2.getString("VAT_FLAG"));
					ballstDTO.setTax_code(rs2.getString("TAX_CODE_FLAG"));
					ballstDTO.setOrg_vat_no(new Long(rs2.getLong("ORG_VAT_NO")));
					ballstDTO.setTax_code_name(rs2.getString("TAX_CODE_NAME"));
					ballstDTO.setVat_krw(new Double(rs2.getDouble("KRW_VAT_SA_AMT")));
					ballstDTO.setVat_usd(new Double(rs2.getDouble("USD_VAT_SA_AMT")));

					//채산항차 추가 170523 GYJ
					//ballstDTO.setVoyage(new Long(rs2.getLong("VOY_NO")));

				//} else if ("G002".equals(Formatter.nullTrim(rs2.getString("TRSACT_CODE")))) {
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150204
				} else if ("G002".equals(Formatter.nullTrim(rs2.getString("TRSACT_CODE"))) || "G004".equals(Formatter.nullTrim(rs2.getString("TRSACT_CODE")))) {
					ballstDTO.setAdd_comm(new Double(rs2.getDouble("SA_RATE")));
					ballstDTO.setAdd_comm_amount_krw(new Double(rs2.getDouble("KRW_SA_AMT")));
					ballstDTO.setAdd_comm_amount_usd(new Double(rs2.getDouble("USD_SA_AMT")));
				}
			}

			result.add(ballstDTO);
			// **************************** Ballast Bonus 가져오기 종료
			// **************************** //



		} catch (Exception e) {

			throw new STXException(e);
		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();
			if (rs2 != null)
				rs2.close();
			if (ps2 != null)
				ps2.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public Collection saBunkerSelect(Long saNo, Connection conn) throws STXException, Exception {


		Collection result = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;

		try {

			StringBuffer sb = new StringBuffer();

			sb.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND  V.trsact_code IN ('B001','B002','B003','B004','B005','B006') ORDER BY V.SA_SEQ ");	//MS FO/DO 추가 190709


			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaBunkerDTO bunkerBODDTO = null;
			Collection BODs = new ArrayList();

			while (rs.next()) {

				if ("B001".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBODDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBODDTO.setBod_fo_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBODDTO.setBod_fo_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBODDTO.setBod_fo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBODDTO.setBod_fo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBODDTO.setBod_fo_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBODDTO.setBod_fo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBODDTO.setBod_fo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBODDTO.setBod_fo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBODDTO.setBod_fo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B002".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBODDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBODDTO.setBod_do_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBODDTO.setBod_do_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBODDTO.setBod_do_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBODDTO.setBod_do_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBODDTO.setBod_do_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBODDTO.setBod_do_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBODDTO.setBod_do_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBODDTO.setBod_do_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBODDTO.setBod_do_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B003".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBODDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBODDTO.setBod_lsfo_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBODDTO.setBod_lsfo_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBODDTO.setBod_lsfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBODDTO.setBod_lsfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBODDTO.setBod_lsfo_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBODDTO.setBod_lsfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBODDTO.setBod_lsfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBODDTO.setBod_lsfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBODDTO.setBod_lsfo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B004".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBODDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBODDTO.setBod_lsdo_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBODDTO.setBod_lsdo_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBODDTO.setBod_lsdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBODDTO.setBod_lsdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBODDTO.setBod_lsdo_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBODDTO.setBod_lsdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBODDTO.setBod_lsdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBODDTO.setBod_lsdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBODDTO.setBod_lsdo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				//MS FO/DO 추가 190709
				} else if ("B005".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBODDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBODDTO.setBod_msfo_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBODDTO.setBod_msfo_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBODDTO.setBod_msfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBODDTO.setBod_msfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBODDTO.setBod_msfo_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBODDTO.setBod_msfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBODDTO.setBod_msfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBODDTO.setBod_msfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBODDTO.setBod_msfo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B006".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBODDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBODDTO.setBod_msdo_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBODDTO.setBod_msdo_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBODDTO.setBod_msdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBODDTO.setBod_msdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBODDTO.setBod_msdo_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBODDTO.setBod_msdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBODDTO.setBod_msdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBODDTO.setBod_msdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBODDTO.setBod_msdo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				}
					BODs.add(bunkerBODDTO);
			}

			result.add(BODs);

			StringBuffer sb1 = new StringBuffer();

			sb1.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb1.append("          FROM OTC_SA_DETAIL V       ");
			sb1.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb1.append(" AND  V.trsact_code IN ('C001','C002','C003','C004','C005','C006') ORDER BY V.SA_SEQ ");	//MS FO/DO 추가 190709



			ps1 = conn.prepareStatement(sb1.toString());
			rs1 = ps1.executeQuery();

			OTCSaBunkerDTO bunkerBORDTO = null;
			Collection BORs = new ArrayList();

			while (rs1.next()) {

				if ("C001".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBORDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBORDTO.setBor_fo_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBORDTO.setBor_fo_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBORDTO.setBor_fo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBORDTO.setBor_fo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBORDTO.setBor_fo_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBORDTO.setBor_fo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBORDTO.setBor_fo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBORDTO.setBor_fo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBORDTO.setBor_fo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("C002".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBORDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBORDTO.setBor_do_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBORDTO.setBor_do_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBORDTO.setBor_do_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBORDTO.setBor_do_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBORDTO.setBor_do_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBORDTO.setBor_do_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBORDTO.setBor_do_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBORDTO.setBor_do_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBORDTO.setBor_do_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("C003".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBORDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBORDTO.setBor_lsfo_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBORDTO.setBor_lsfo_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBORDTO.setBor_lsfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBORDTO.setBor_lsfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBORDTO.setBor_lsfo_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBORDTO.setBor_lsfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBORDTO.setBor_lsfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBORDTO.setBor_lsfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBORDTO.setBor_lsfo_tax_code_name(rs.getString("TAX_CODE_NAME"));


				} else if ("C004".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBORDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBORDTO.setBor_lsdo_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBORDTO.setBor_lsdo_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBORDTO.setBor_lsdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBORDTO.setBor_lsdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBORDTO.setBor_lsdo_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBORDTO.setBor_lsdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBORDTO.setBor_lsdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBORDTO.setBor_lsdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBORDTO.setBor_lsdo_tax_code_name(rs.getString("TAX_CODE_NAME"));
				}

				//MS FO/DO 추가 190709
				else if ("C005".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBORDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBORDTO.setBor_msfo_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBORDTO.setBor_msfo_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBORDTO.setBor_msfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBORDTO.setBor_msfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBORDTO.setBor_msfo_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBORDTO.setBor_msfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBORDTO.setBor_msfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBORDTO.setBor_msfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBORDTO.setBor_msfo_tax_code_name(rs.getString("TAX_CODE_NAME"));


				} else if ("C006".equals(rs.getString("TRSACT_CODE"))) {

					bunkerBORDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bunkerBORDTO.setBor_msdo_qty(new Double(rs.getDouble("BNK_QTY")));
					bunkerBORDTO.setBor_msdo_price(new Double(rs.getDouble("BNK_PRC")));
					bunkerBORDTO.setBor_msdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bunkerBORDTO.setBor_msdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bunkerBORDTO.setBor_msdo_vat_flag(rs.getString("VAT_FLAG"));
					bunkerBORDTO.setBor_msdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bunkerBORDTO.setBor_msdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bunkerBORDTO.setBor_msdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bunkerBORDTO.setBor_msdo_tax_code_name(rs.getString("TAX_CODE_NAME"));
				}

				BORs.add(bunkerBORDTO);
			}

			result.add(BORs);

		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public OTCSaBunkerDTO saBunkerSelect_20080722(Long saNo, Connection conn) throws STXException, Exception {

		OTCSaBunkerDTO result = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND  V.trsact_code IN ('B001','B002','B003','B004','C001','C002','C003','C004','B005','B006','C005','C006') ORDER BY V.SA_SEQ ");	//MS FO/DO 추가 190709



			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();
			int row = 0;

			while (rs.next()) {
				if (row == 0)
					result = new OTCSaBunkerDTO();
				if ("B001".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_fo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_fo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_fo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_fo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_fo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_fo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_fo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_fo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_fo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B002".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_do_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_do_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_do_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_do_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_do_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_do_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_do_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_do_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_do_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B003".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_lsfo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_lsfo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_lsfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_lsfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_lsfo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_lsfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_lsfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_lsfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_lsfo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B004".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_lsdo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_lsdo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_lsdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_lsdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_lsdo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_lsdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_lsdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_lsdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_lsdo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				//MS FO/DO 추가 190709
				} else if ("B005".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_msfo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_msfo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_msfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_msfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_msfo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_msfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_msfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_msfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_msfo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B006".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_msdo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_msdo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_msdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_msdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_msdo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_msdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_msdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_msdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_msdo_tax_code_name(rs.getString("TAX_CODE_NAME"));


				} else if ("C001".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_fo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_fo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_fo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_fo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_fo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_fo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_fo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_fo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_fo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("C002".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_do_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_do_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_do_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_do_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_do_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_do_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_do_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_do_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_do_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("C003".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_lsfo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_lsfo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_lsfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_lsfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_lsfo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_lsfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_lsfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_lsfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_lsfo_tax_code_name(rs.getString("TAX_CODE_NAME"));


				} else if ("C004".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_lsdo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_lsdo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_lsdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_lsdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_lsdo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_lsdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_lsdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_lsdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_lsdo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				//MS FO/DO 추가 190709
				} else if ("C005".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_msfo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_msfo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_msfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_msfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_msfo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_msfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_msfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_msfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_msfo_tax_code_name(rs.getString("TAX_CODE_NAME"));


				} else if ("C006".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_msdo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_msdo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_msdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_msdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_msdo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_msdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_msdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_msdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_msdo_tax_code_name(rs.getString("TAX_CODE_NAME"));
				}


				row = row + 1;
			}



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}
	/**
	 * <p> By KGW 20080721...BOD/BOR 분리
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public OTCSaBunkerDTO saBunkerBODSelect_20080723(Long saNo, Connection conn) throws STXException, Exception {

		OTCSaBunkerDTO result = null;


		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND  V.trsact_code IN ('B001','B002','B003','B004','B005','B006') ORDER BY V.SA_SEQ ");	//MS FO/DO 추가 190709



			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();
			int row = 0;

			while (rs.next()) {
				if (row == 0)
					result = new OTCSaBunkerDTO();
				if ("B001".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_fo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_fo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_fo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_fo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_fo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_fo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_fo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_fo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_fo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B002".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_do_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_do_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_do_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_do_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_do_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_do_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_do_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_do_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_do_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B003".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_lsfo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_lsfo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_lsfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_lsfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_lsfo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_lsfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_lsfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_lsfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_lsfo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B004".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_lsdo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_lsdo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_lsdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_lsdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_lsdo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_lsdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_lsdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_lsdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_lsdo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				//MS FO/DO 추가 190709
				} else if ("B005".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_msfo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_msfo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_msfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_msfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_msfo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_msfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_msfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_msfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_msfo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B006".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBod_msdo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBod_msdo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBod_msdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBod_msdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBod_msdo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBod_msdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBod_msdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBod_msdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBod_msdo_tax_code_name(rs.getString("TAX_CODE_NAME"));
				}


				row = row + 1;
			}



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}
	public Collection saBunkerBODSelect(Long saNo, Connection conn) throws STXException, Exception {

		Collection result = new ArrayList();
		OTCSaBunkerDTO bodDTO = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND  V.trsact_code IN ('B001','B002','B003','B004','B005','B006') ORDER BY V.GROUP_SEQ ");	//MS FO/DO 추가 190709

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			int row = 0;
			long group_seq = 0;
			long pre_group_seq = 0;

			while (rs.next()) {
				group_seq = rs.getLong("GROUP_SEQ");
				if (row == 0) {
					bodDTO = new OTCSaBunkerDTO();
					pre_group_seq = group_seq;
				} else if (group_seq != pre_group_seq) {
					result.add(bodDTO);
					bodDTO = new OTCSaBunkerDTO();
				}

				if ("B001".equals(rs.getString("TRSACT_CODE"))) {

					bodDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bodDTO.setBod_fo_qty(new Double(rs.getDouble("BNK_QTY")));
					bodDTO.setBod_fo_price(new Double(rs.getDouble("BNK_PRC")));
					bodDTO.setBod_fo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bodDTO.setBod_fo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bodDTO.setBod_fo_vat_flag(rs.getString("VAT_FLAG"));
					bodDTO.setBod_fo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bodDTO.setBod_fo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					bodDTO.setBod_fo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bodDTO.setBod_fo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bodDTO.setBod_fo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B002".equals(rs.getString("TRSACT_CODE"))) {

					bodDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bodDTO.setBod_do_qty(new Double(rs.getDouble("BNK_QTY")));
					bodDTO.setBod_do_price(new Double(rs.getDouble("BNK_PRC")));
					bodDTO.setBod_do_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bodDTO.setBod_do_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bodDTO.setBod_do_vat_flag(rs.getString("VAT_FLAG"));
					bodDTO.setBod_do_tax_code(rs.getString("TAX_CODE_FLAG"));
					bodDTO.setBod_do_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					bodDTO.setBod_do_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bodDTO.setBod_do_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bodDTO.setBod_do_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B003".equals(rs.getString("TRSACT_CODE"))) {

					bodDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bodDTO.setBod_lsfo_qty(new Double(rs.getDouble("BNK_QTY")));
					bodDTO.setBod_lsfo_price(new Double(rs.getDouble("BNK_PRC")));
					bodDTO.setBod_lsfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bodDTO.setBod_lsfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bodDTO.setBod_lsfo_vat_flag(rs.getString("VAT_FLAG"));
					bodDTO.setBod_lsfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bodDTO.setBod_lsfo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					bodDTO.setBod_lsfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bodDTO.setBod_lsfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bodDTO.setBod_lsfo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B004".equals(rs.getString("TRSACT_CODE"))) {

					bodDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bodDTO.setBod_lsdo_qty(new Double(rs.getDouble("BNK_QTY")));
					bodDTO.setBod_lsdo_price(new Double(rs.getDouble("BNK_PRC")));
					bodDTO.setBod_lsdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bodDTO.setBod_lsdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bodDTO.setBod_lsdo_vat_flag(rs.getString("VAT_FLAG"));
					bodDTO.setBod_lsdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bodDTO.setBod_lsdo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					bodDTO.setBod_lsdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bodDTO.setBod_lsdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bodDTO.setBod_lsdo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				//MS FO/DO 추가 190709
				} else if ("B005".equals(rs.getString("TRSACT_CODE"))) {

					bodDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bodDTO.setBod_msfo_qty(new Double(rs.getDouble("BNK_QTY")));
					bodDTO.setBod_msfo_price(new Double(rs.getDouble("BNK_PRC")));
					bodDTO.setBod_msfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bodDTO.setBod_msfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bodDTO.setBod_msfo_vat_flag(rs.getString("VAT_FLAG"));
					bodDTO.setBod_msfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bodDTO.setBod_msfo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					bodDTO.setBod_msfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bodDTO.setBod_msfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bodDTO.setBod_msfo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("B006".equals(rs.getString("TRSACT_CODE"))) {

					bodDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					bodDTO.setBod_msdo_qty(new Double(rs.getDouble("BNK_QTY")));
					bodDTO.setBod_msdo_price(new Double(rs.getDouble("BNK_PRC")));
					bodDTO.setBod_msdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					bodDTO.setBod_msdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					bodDTO.setBod_msdo_vat_flag(rs.getString("VAT_FLAG"));
					bodDTO.setBod_msdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					bodDTO.setBod_msdo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					bodDTO.setBod_msdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					bodDTO.setBod_msdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					bodDTO.setBod_msdo_tax_code_name(rs.getString("TAX_CODE_NAME"));


				}
				row = row + 1;
				pre_group_seq = group_seq;
			}

			if(row > 0 ) {
				result.add(bodDTO);
			}

		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}
	/**
	 * <p> By KGW 20080721...BOD/BOR 분리
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public OTCSaBunkerDTO saBunkerBORSelect_20080723(Long saNo, Connection conn) throws STXException, Exception {

		OTCSaBunkerDTO result = null;


		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND  V.trsact_code IN ('C001','C002','C003','C004','C005','C006') ORDER BY V.SA_SEQ ");	//MS FO/DO 추가 190709



			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();
			int row = 0;

			while (rs.next()) {
				if (row == 0)
					result = new OTCSaBunkerDTO();

				if ("C001".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_fo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_fo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_fo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_fo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_fo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_fo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_fo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_fo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_fo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("C002".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_do_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_do_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_do_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_do_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_do_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_do_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_do_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_do_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_do_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("C003".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_lsfo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_lsfo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_lsfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_lsfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_lsfo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_lsfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_lsfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_lsfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_lsfo_tax_code_name(rs.getString("TAX_CODE_NAME"));


				} else if ("C004".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_lsdo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_lsdo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_lsdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_lsdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_lsdo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_lsdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_lsdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_lsdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_lsdo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				//MS FO/DO 추가 190709
				} else if ("C005".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_msfo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_msfo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_msfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_msfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_msfo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_msfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_msfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_msfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_msfo_tax_code_name(rs.getString("TAX_CODE_NAME"));


				} else if ("C006".equals(rs.getString("TRSACT_CODE"))) {

					result.setSa_no(new Double(rs.getDouble("SA_NO")));
					result.setBor_msdo_qty(new Double(rs.getDouble("BNK_QTY")));
					result.setBor_msdo_price(new Double(rs.getDouble("BNK_PRC")));
					result.setBor_msdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					result.setBor_msdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					result.setBor_msdo_vat_flag(rs.getString("VAT_FLAG"));
					result.setBor_msdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					result.setBor_msdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					result.setBor_msdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					result.setBor_msdo_tax_code_name(rs.getString("TAX_CODE_NAME"));
				}


				row = row + 1;
			}



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}

	public Collection saBunkerBORSelect(Long saNo, Connection conn) throws STXException, Exception {

		Collection result = new ArrayList();
		OTCSaBunkerDTO borDTO = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND  V.trsact_code IN ('C001','C002','C003','C004','C005','C006') ORDER BY V.SA_SEQ ");	//MS FO/DO 추가 190709

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();


			int row = 0;
			long group_seq = 0;
			long pre_group_seq = 0;

			while (rs.next()) {

				group_seq = rs.getLong("GROUP_SEQ");
				if (row == 0) {
					borDTO = new OTCSaBunkerDTO();
					pre_group_seq = group_seq;
				} else if (group_seq != pre_group_seq) {
					result.add(borDTO);
					borDTO = new OTCSaBunkerDTO();
				}

					if ("C001".equals(rs.getString("TRSACT_CODE"))) {

					borDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					borDTO.setBor_fo_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_fo_price(new Double(rs.getDouble("BNK_PRC")));
					borDTO.setBor_fo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					borDTO.setBor_fo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					borDTO.setBor_fo_vat_flag(rs.getString("VAT_FLAG"));
					borDTO.setBor_fo_tax_code(rs.getString("TAX_CODE_FLAG"));
					borDTO.setBor_fo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					borDTO.setBor_fo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					borDTO.setBor_fo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					borDTO.setBor_fo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("C002".equals(rs.getString("TRSACT_CODE"))) {

					borDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					borDTO.setBor_do_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_do_price(new Double(rs.getDouble("BNK_PRC")));
					borDTO.setBor_do_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					borDTO.setBor_do_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					borDTO.setBor_do_vat_flag(rs.getString("VAT_FLAG"));
					borDTO.setBor_do_tax_code(rs.getString("TAX_CODE_FLAG"));
					borDTO.setBor_do_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					borDTO.setBor_do_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					borDTO.setBor_do_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					borDTO.setBor_do_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("C003".equals(rs.getString("TRSACT_CODE"))) {

					borDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					borDTO.setBor_lsfo_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_lsfo_price(new Double(rs.getDouble("BNK_PRC")));
					borDTO.setBor_lsfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					borDTO.setBor_lsfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					borDTO.setBor_lsfo_vat_flag(rs.getString("VAT_FLAG"));
					borDTO.setBor_lsfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					borDTO.setBor_lsfo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					borDTO.setBor_lsfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					borDTO.setBor_lsfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					borDTO.setBor_lsfo_tax_code_name(rs.getString("TAX_CODE_NAME"));


				} else if ("C004".equals(rs.getString("TRSACT_CODE"))) {

					borDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					borDTO.setBor_lsdo_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_lsdo_price(new Double(rs.getDouble("BNK_PRC")));
					borDTO.setBor_lsdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					borDTO.setBor_lsdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					borDTO.setBor_lsdo_vat_flag(rs.getString("VAT_FLAG"));
					borDTO.setBor_lsdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					borDTO.setBor_lsdo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					borDTO.setBor_lsdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					borDTO.setBor_lsdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					borDTO.setBor_lsdo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				//MS FO/DO 추가 190709
				} else if ("C005".equals(rs.getString("TRSACT_CODE"))) {

					borDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					borDTO.setBor_msfo_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_msfo_price(new Double(rs.getDouble("BNK_PRC")));
					borDTO.setBor_msfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					borDTO.setBor_msfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					borDTO.setBor_msfo_vat_flag(rs.getString("VAT_FLAG"));
					borDTO.setBor_msfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					borDTO.setBor_msfo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					borDTO.setBor_msfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					borDTO.setBor_msfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					borDTO.setBor_msfo_tax_code_name(rs.getString("TAX_CODE_NAME"));


				} else if ("C006".equals(rs.getString("TRSACT_CODE"))) {

					borDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					borDTO.setBor_msdo_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_msdo_price(new Double(rs.getDouble("BNK_PRC")));
					borDTO.setBor_msdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					borDTO.setBor_msdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					borDTO.setBor_msdo_vat_flag(rs.getString("VAT_FLAG"));
					borDTO.setBor_msdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					borDTO.setBor_msdo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					borDTO.setBor_msdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					borDTO.setBor_msdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					borDTO.setBor_msdo_tax_code_name(rs.getString("TAX_CODE_NAME"));
				}

				row = row + 1;
				pre_group_seq = group_seq;

			}

			if(row > 0 ) {
				result.add(borDTO);
			}


		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}


	public Collection saBunkerBehalfSelect(Long saNo, Connection conn) throws STXException, Exception {

		Collection result = new ArrayList();
		OTCSaBunkerDTO behalfDTO = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append(" SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND  V.trsact_code IN ('E001','E002','E003','E004') ORDER BY V.SA_SEQ ");

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();


			int row = 0;
			long group_seq = 0;
			long pre_group_seq = 0;

			while (rs.next()) {

				group_seq = rs.getLong("GROUP_SEQ");
				if (row == 0) {
					behalfDTO = new OTCSaBunkerDTO();
					pre_group_seq = group_seq;
				} else if (group_seq != pre_group_seq) {
					result.add(behalfDTO);
					behalfDTO = new OTCSaBunkerDTO();
				}

					if ("E001".equals(rs.getString("TRSACT_CODE"))) {

					behalfDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					behalfDTO.setBehalf_fo_qty(new Double(rs.getDouble("BNK_QTY")));
					behalfDTO.setBehalf_fo_price(new Double(rs.getDouble("BNK_PRC")));
					behalfDTO.setBehalf_fo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					behalfDTO.setBehalf_fo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					behalfDTO.setBehalf_fo_vat_flag(rs.getString("VAT_FLAG"));
					behalfDTO.setBehalf_fo_tax_code(rs.getString("TAX_CODE_FLAG"));
					behalfDTO.setBehalf_fo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					behalfDTO.setBehalf_fo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					behalfDTO.setBehalf_fo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					behalfDTO.setBehalf_fo_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("E002".equals(rs.getString("TRSACT_CODE"))) {

					behalfDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					behalfDTO.setBehalf_do_qty(new Double(rs.getDouble("BNK_QTY")));
					behalfDTO.setBehalf_do_price(new Double(rs.getDouble("BNK_PRC")));
					behalfDTO.setBehalf_do_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					behalfDTO.setBehalf_do_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					behalfDTO.setBehalf_do_vat_flag(rs.getString("VAT_FLAG"));
					behalfDTO.setBehalf_do_tax_code(rs.getString("TAX_CODE_FLAG"));
					behalfDTO.setBehalf_do_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					behalfDTO.setBehalf_do_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					behalfDTO.setBehalf_do_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					behalfDTO.setBehalf_do_tax_code_name(rs.getString("TAX_CODE_NAME"));

				} else if ("E003".equals(rs.getString("TRSACT_CODE"))) {

					behalfDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					behalfDTO.setBehalf_lsfo_qty(new Double(rs.getDouble("BNK_QTY")));
					behalfDTO.setBehalf_lsfo_price(new Double(rs.getDouble("BNK_PRC")));
					behalfDTO.setBehalf_lsfo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					behalfDTO.setBehalf_lsfo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					behalfDTO.setBehalf_lsfo_vat_flag(rs.getString("VAT_FLAG"));
					behalfDTO.setBehalf_lsfo_tax_code(rs.getString("TAX_CODE_FLAG"));
					behalfDTO.setBehalf_lsfo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					behalfDTO.setBehalf_lsfo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					behalfDTO.setBehalf_lsfo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					behalfDTO.setBehalf_lsfo_tax_code_name(rs.getString("TAX_CODE_NAME"));


				} else if ("E004".equals(rs.getString("TRSACT_CODE"))) {

					behalfDTO.setSa_no(new Double(rs.getDouble("SA_NO")));
					behalfDTO.setBehalf_lsdo_qty(new Double(rs.getDouble("BNK_QTY")));
					behalfDTO.setBehalf_lsdo_price(new Double(rs.getDouble("BNK_PRC")));
					behalfDTO.setBehalf_lsdo_amount_krw(new Double(rs.getDouble("KRW_SA_AMT")));
					behalfDTO.setBehalf_lsdo_amount_usd(new Double(rs.getDouble("USD_SA_AMT")));
					behalfDTO.setBehalf_lsdo_vat_flag(rs.getString("VAT_FLAG"));
					behalfDTO.setBehalf_lsdo_tax_code(rs.getString("TAX_CODE_FLAG"));
					behalfDTO.setBehalf_lsdo_org_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
					behalfDTO.setBehalf_lsdo_vat_krw(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
					behalfDTO.setBehalf_lsdo_vat_usd(new Double(rs.getDouble("USD_VAT_SA_AMT")));
					behalfDTO.setBehalf_lsdo_tax_code_name(rs.getString("TAX_CODE_NAME"));
				}

				row = row + 1;
				pre_group_seq = group_seq;

			}

			if(row > 0 ) {
				result.add(behalfDTO);
			}


		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}

	public Collection saBunkerFirstBODSelect(String vslCode, Long voyNo, String tcFlag, Connection conn) throws STXException, Exception {

		Collection result = new ArrayList();
		OTCSaBunkerDTO borDTO = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append("\n   SELECT D.TRSACT_CODE, D.BNK_QTY, D.BNK_PRC	, 			  ");
			sb.append("\n   DENSE_RANK() OVER (ORDER BY H.SA_NO, D.GROUP_SEQ) AS GROUP_SEQ			  ");
			sb.append("\n   FROM OTC_SA_DETAIL D, OTC_SA_HEAD H       				  ");
			sb.append("\n   WHERE H.SA_NO = D.SA_NO							  ");
			sb.append("\n   AND H.VSL_CODE = '"  + vslCode + "'								  ");
			sb.append("\n   AND H.VOY_NO = "+ voyNo + "									  ");
			sb.append("\n   AND H.CANCEL_FLAG = 'N'								  ");
			sb.append("\n   AND DECODE(H.CHT_IN_OUT_CODE, 'R', 'O', H.CHT_IN_OUT_CODE) = '" + tcFlag + "'  ");
			sb.append("\n   AND  D.TRSACT_CODE IN ('B001','B002','B003','B004','B005','B006') 				  ");	//MS FO/DO 추가 190709
			sb.append("\n   ORDER BY D.SA_SEQ 									  ");

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();


			int row = 0;
			long group_seq = 0;
			long pre_group_seq = 0;

			while (rs.next()) {

				group_seq = rs.getLong("GROUP_SEQ");
				if (row == 0) {
					borDTO = new OTCSaBunkerDTO();
					pre_group_seq = group_seq;
				} else if (group_seq != pre_group_seq) {
					result.add(borDTO);
					borDTO = new OTCSaBunkerDTO();
				}

					if ("B001".equals(rs.getString("TRSACT_CODE"))) {
					borDTO.setBor_fo_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_fo_price(new Double(rs.getDouble("BNK_PRC")));

				} else if ("B002".equals(rs.getString("TRSACT_CODE"))) {
					borDTO.setBor_do_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_do_price(new Double(rs.getDouble("BNK_PRC")));

				} else if ("B003".equals(rs.getString("TRSACT_CODE"))) {
					borDTO.setBor_lsfo_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_lsfo_price(new Double(rs.getDouble("BNK_PRC")));

				} else if ("B004".equals(rs.getString("TRSACT_CODE"))) {
					borDTO.setBor_lsdo_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_lsdo_price(new Double(rs.getDouble("BNK_PRC")));

				//MS FO/DO 추가 190709
				} else if ("B005".equals(rs.getString("TRSACT_CODE"))) {
					borDTO.setBor_msfo_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_msfo_price(new Double(rs.getDouble("BNK_PRC")));

				} else if ("B006".equals(rs.getString("TRSACT_CODE"))) {
					borDTO.setBor_msdo_qty(new Double(rs.getDouble("BNK_QTY")));
					borDTO.setBor_msdo_price(new Double(rs.getDouble("BNK_PRC")));
				}

				row = row + 1;
				pre_group_seq = group_seq;

			}

			if(row > 0 ) {
				result.add(borDTO);
			}


		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}


	/**
	 * <p>
	 * 설명:saHead내역을 삭제하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA HEAD 테이블에 삭제할 시 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saHeadDelete 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public String saDetailDelete(Long saNo, Long saSeq, Connection conn) throws STXException {

		String result = "";


		try {
			DbWrap dbWrap = new DbWrap();

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append(" SELECT  V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND SA_SEQ = " + saSeq.longValue() + " ");

			dbWrap.setObject(conn, OTCSaDetailVO.class, sb.toString(), 4);

			result = "SUC-0600";

		} catch (Exception e) {
			throw new STXException(e);
		}
		return result;
	}

	public String saBbcLngDetailDelete(Long saNo, Connection conn) throws STXException {

		String result = "";


		try {
			DbWrap dbWrap = new DbWrap();

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append(" SELECT  V.*   ");
			sb.append("          FROM OTC_SA_DETAIL V       ");
			sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");


			dbWrap.setObject(conn, OTCSaDetailVO.class, sb.toString(), 4);

			result = "SUC-0600";

		} catch (Exception e) {
			throw new STXException(e);
		}
		return result;
	}

	/**
	 * <p>
	 * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 open item 을 읽어온다. openType : OE ,
	 * RB, AP, AC, AR chtInOutCOde : O, T, R
	 */
	public Collection saOpenItemSearch(String vslCode, Long voyNo, String chtInOutCode, String accCode, String teamCode, String openType, String openTab, String postingDate, String stl_flag, UserBean userBean, Connection conn) throws Exception, STXException {

		Collection result = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;

		stl_flag = Formatter.nullTrim(stl_flag);



		try {

			String invoice = " select a.* , ";
			invoice = invoice + "	 (nvl(a.entered_balance_amount,0)-  nvl(a.entered_pending_amount,0)) as entered_amt, ";
			invoice = invoice + "	 (nvl(a.usd_balance_amount,0)- nvl(a.usd_pending_amount,0)) as usd_amt, ";
			invoice = invoice + "	 (nvl(a.krw_balance_amount,0)- nvl(a.krw_pending_amount,0)) as won_amt ";
			invoice = invoice + "	  from  ear_if_invoice_balance_v a  ";

			String trx = " select a.* , ";
			trx = trx + "	 (nvl(a.entered_balance_amount,0)- nvl(a.entered_pending_amount,0)) as entered_amt, ";
			trx = trx + "	 (nvl(a.usd_balance_amount,0)- nvl(a.usd_pending_amount,0)) as usd_amt, ";
			trx = trx + "	 (nvl(a.krw_balance_amount,0)- nvl(a.krw_pending_amount,0)) as won_amt  ";
			trx = trx + "	 from  ear_if_trx_balance_v a  ";

			StringBuffer sb = new StringBuffer();
			if ("OE".equals(openType) || "RB".equals(openType) || "AP".equals(openType)) {
				sb.append(invoice);
				sb.append(" where 1= 1 ");
				//sb.append(" and a.segment4 > ' ' and a.segment5 >'0' ");
				sb.append(" and a.segment4 > ' ' ");          //100701 GYJ VOY를 0부터 가져옴
				if (!"".equals(postingDate)) {

					sb.append(" and a.gl_date  <= to_date('" + postingDate + "' ,'yyyymmdd') ");
				}

			} else if ("AC".equals(openType) || "AR".equals(openType)) {
				sb.append(trx);
				sb.append(" where 1= 1 ");
				//sb.append(" and a.segment4 > ' ' and a.segment5 >'0' ");
				sb.append(" and a.segment4 > ' ' ");  			//100701 GYJ VOY를 0부터 가져옴
				if (!"".equals(postingDate)) {
					sb.append(" and a.gl_date  <= to_date('" + postingDate + "' ,'yyyymmdd') ");
				}

			} else if ("".equals(openType)) {

				sb.append(" 	 SELECT * FROM(	 ");
				sb.append(" 	select a.EXCHANGE_RATE_KRW, a.EXCHANGE_RATE_USD, a.EXCHANGE_RATE_DATE_KRW, a.SEGMENT4 as SEGMENT4 , a.SEGMENT5 as SEGMENT5, a.CURRENCY_CODE as CURRENCY_CODE, ");
				sb.append(" 	a.SEGMENT2 as SEGMENT2, a.PORT_CODE as PORT_CODE , a.VENDOR_NAME as CUSTOMER_NAME,  ");
				sb.append(" 	a.CUSTOMER_UNIQUE_ID as CUSTOMER_UNIQUE_ID , a.INVOICE_NUMBER as INVOICE_NUMBER,  ");
				sb.append(" 	a.SEGMENT3 as SEGMENT3 , a.COMMENTS as COMMENTS , ");
				sb.append(" 	a.GL_DATE , ");
				sb.append(" 	(nvl(a.entered_balance_amount,0)-  nvl(a.entered_pending_amount,0)) as entered_amt,  ");
				sb.append(" 	(nvl(a.usd_balance_amount,0)- nvl(a.usd_pending_amount,0)) as usd_amt,  ");
				sb.append(" 	(nvl(a.krw_balance_amount,0)- nvl(a.krw_pending_amount,0)) as won_amt  ");
				sb.append(" 	from  ear_if_invoice_balance_v a  ");
				sb.append(" where 1= 1 ");
				//sb.append(" and a.segment4 > ' ' and a.segment5 >'0' ");
				sb.append(" and a.segment4 > ' '  ");			//100701 GYJ VOY를 0부터 가져옴
				if (!"".equals(postingDate)) {

					sb.append(" and a.gl_date  <= to_date('" + postingDate + "' ,'yyyymmdd') ");
				}
				if ("L".equals(stl_flag)) {
					sb.append(" and a.segment3  IN ( '210802' ,'210805' ,'210809','210405', '210701','210402','210803','210403' ,'210809','210405','210701' ,'210499', '110599')");  //111013 GYJ 기타 영업미지급금, 영업미수금-기타잔액 추가.

				} else if ("T".equals(chtInOutCode)) {
					sb.append(" and a.segment3  IN ( '210802' ,'210805' ,'210809','210405', '210701','210402' ,'210499', '110599')");   //111013 GYJ 기타 영업미지급금, 영업미수금-기타잔액 추가.

				} else {
					sb.append(" and a.segment3 IN ( '210803','210403' ,'210809','210405','210701','210499, '110599' ) ");   //111013 GYJ 기타 영업미지급금, 영업미수금-기타잔액 추가.

				}
			}

			if ("OE".equals(openType)) {
				if ("L".equals(stl_flag)) {
					sb.append(" and a.segment3 in ('210802' ,'210803','210809') ");
				} else if ("T".equals(chtInOutCode)) {
					sb.append(" and a.segment3 in ('210802','210809') ");
				} else {
					sb.append(" and a.segment3 in ('210803','210809')");
				}
			} else if ("RB".equals(openType)) {

				sb.append(" and a.segment3 = '210805'");

			} else if ("AP".equals(openType)) {
				//111013 GYJ '210499' - 기타 영업미지급금 추가.
				if ("L".equals(stl_flag)) {
					sb.append("  and a.segment3  IN ('210402' ,'210405','210701', '210403','210499' )");
				} else if ("T".equals(chtInOutCode)) {
					sb.append("  and a.segment3  IN ('210402' ,'210405','210701', '210499')");

				} else {
					sb.append("  and a.segment3 IN ('210403' ,'210405','210701','210499' )");

				}
			} else if ("AC".equals(openType)) {

				// 계정추가(110913)_회계요청  hjkang 20081106

				if ("L".equals(stl_flag)) {

					sb.append(" and a.segment3  IN ('110902' , '110912', '110903', '110913' )");
					//sb.append(" and a.segment3  IN ('110902' , '110912', '110903' )");

				} else if ("T".equals(chtInOutCode)) {

					sb.append(" and a.segment3  IN ('110902' , '110912', '110913')");
					//sb.append(" and a.segment3  IN ('110902' , '110912')");
				} else {

					sb.append(" and a.segment3 IN ('110903' , '110912', '110913') ");
					//sb.append(" and a.segment3 IN ('110903' , '110912') ");
				}
			} else if ("AR".equals(openType)) {
				//111013 GYJ 110599 - 영업미수금-기타잔액 추가
				if ("L".equals(stl_flag)) {
					sb.append(" and a.segment3  IN ( '110503', '110502' ,'110599') ");
				} else if ("T".equals(chtInOutCode)) {
					sb.append(" and a.segment3  IN ('110503','110599') ");
				} else {
					sb.append(" and a.segment3 IN ('110502', '110599')");
				}
			}

			if (!"".equals(Formatter.nullTrim(vslCode))) {
				sb.append(" and  a.segment4 = '" + Formatter.nullTrim(vslCode) + "' ");
			}

			if (Formatter.nullLong(voyNo) != 0) {
				sb.append(" and  a.segment5 = '" + String.valueOf(Formatter.nullLong(voyNo)) + "' ");
			}

			if (!"".equals(Formatter.nullTrim(accCode))) {
				sb.append(" and  a.customer_unique_id = '" + Formatter.nullTrim(accCode) + "' ");
			}

			if (!"".equals(Formatter.nullTrim(teamCode))) {
				sb.append(" and  (a.segment2 = '" + Formatter.nullTrim(teamCode) + "' or a.new_dept_code = '" + Formatter.nullTrim(teamCode) + "')  ");
			}

			sb.append(" and  ( a.source_system   <>  'CNTR'  or (a.source_system   =  'CNTR' and substr(a.source_trx_number,9,4) IN ('CFDE','CADE','CDDE') ) ) ");
			sb.append(" and  ( (nvl(a.entered_balance_amount,0)-  nvl(a.entered_pending_amount,0))  >  0 OR  (nvl(a.entered_balance_amount,0)-  nvl(a.entered_pending_amount,0))  <  0 )");

			if ("".equals(openType)) {

				sb.append("  UNION ALL ");
				sb.append(" select a.EXCHANGE_RATE_KRW, a.EXCHANGE_RATE_USD, a.EXCHANGE_RATE_DATE_KRW, a.SEGMENT4 as SEGMENT4 , a.SEGMENT5 as SEGMENT5, a.CURRENCY_CODE as CURRENCY_CODE,");
				sb.append(" a.SEGMENT2 as SEGMENT2, a.PORT_CODE as PORT_CODE , a.CUSTOMER_NAME as CUSTOMER_NAME,");
				sb.append(" a.CUSTOMER_UNIQUE_ID as CUSTOMER_UNIQUE_ID , a.TRX_NUMBER as INVOICE_NUMBER,");
				sb.append(" a.SEGMENT3 as SEGMENT3 , a.COMMENTS as COMMENTS , ");
				sb.append(" 	a.GL_DATE , ");
				sb.append(" (nvl(a.entered_balance_amount,0)- nvl(a.entered_pending_amount,0)) as entered_amt, ");
				sb.append(" (nvl(a.usd_balance_amount,0)- nvl(a.usd_pending_amount,0)) as usd_amt, ");
				sb.append("  (nvl(a.krw_balance_amount,0)- nvl(a.krw_pending_amount,0)) as won_amt ");
				sb.append("  from  ear_if_trx_balance_v a  ");
				sb.append(" where 1= 1 ");
				//sb.append(" and a.segment4 > ' ' and a.segment5 >'0' ");			//100701 GYJ VOY를 0부터 가져옴
				sb.append(" and a.segment4 > ' '  ");
				if (!"".equals(postingDate)) {

					sb.append(" and a.gl_date  <= to_date('" + postingDate + "' ,'yyyymmdd') ");
				}

				//111013 GYJ 110599 - 영업미수-기타잔액 추가.
				if ("L".equals(stl_flag)) {

					sb.append(" and a.segment3  IN ( '110902' ,  '110912', '110503' ,'110903'  ,'110502','110913' ,'110599')");

				} else if ("T".equals(chtInOutCode)) {

					sb.append(" and a.segment3  IN ( '110902' ,  '110912', '110503','110913','110599' )");
				} else {

					sb.append(" and a.segment3 IN ( '110903' , '110912' ,'110502','110913','110599') ");

				}

				if (!"".equals(Formatter.nullTrim(vslCode))) {
					sb.append(" and  a.segment4 = '" + Formatter.nullTrim(vslCode) + "' ");
				}

				if (Formatter.nullLong(voyNo) != 0) {
					sb.append(" and  a.segment5 = '" + String.valueOf(Formatter.nullLong(voyNo)) + "' ");
				}

				if (!"".equals(Formatter.nullTrim(accCode))) {
					sb.append(" and  a.customer_unique_id = '" + Formatter.nullTrim(accCode) + "' ");
				}

				if (!"".equals(Formatter.nullTrim(teamCode))) {
					sb.append(" and  (a.segment2 = '" + Formatter.nullTrim(teamCode) + "' or a.new_dept_code = '" + Formatter.nullTrim(teamCode) + "') ");
				}

				sb.append(" and  ( a.source_system   <>  'CNTR'  or (a.source_system   =  'CNTR' and substr(a.source_trx_number,9,4) IN ('CFDE','CADE','CDDE') )) ");
				sb.append(" and  ( (nvl(a.entered_balance_amount,0)-  nvl(a.entered_pending_amount,0))  >  0 OR  (nvl(a.entered_balance_amount,0)-  nvl(a.entered_pending_amount,0))  <  0 )");

				sb.append(" ) ");

			}


			log.debug(">> saOpenItemSearch 쿼리문 \n : " + sb.toString() );

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaOpenItemDTO openDTO = null;
			OTCSaOwnSettleDTO stlDTO = null;
			String glAcd = "";
			double usd_rate = 0;
			double krw_rate = 0;
			while (rs.next()) {
				if ("OP".equals(openTab)) {
					openDTO = new OTCSaOpenItemDTO();

					if ("OE".equals(openType) || "RB".equals(openType) || "AP".equals(openType)) {
						glAcd = Formatter.nullTrim(rs.getString("SEGMENT3"));
						openDTO.setItem_gubun(saOpenType(glAcd));
						openDTO.setVsl(Formatter.nullTrim(rs.getString("SEGMENT4")));
						openDTO.setVoy(Formatter.nullTrim(rs.getString("SEGMENT5")));
						openDTO.setCurrency_code(Formatter.nullTrim(rs.getString("CURRENCY_CODE")));
						openDTO.setEntered_amt(new Double(rs.getDouble("ENTERED_AMT")));
						openDTO.setUsd_amt(new Double(rs.getDouble("USD_AMT")));
						openDTO.setWon_amt(new Double(rs.getDouble("WON_AMT")));
						openDTO.setDept(Formatter.nullTrim(rs.getString("SEGMENT2")));
						openDTO.setPort(Formatter.nullTrim(rs.getString("PORT_CODE")));
						openDTO.setStl_port_code(Formatter.nullTrim(rs.getString("PORT_CODE")));
						openDTO.setAcc_name(Formatter.nullTrim(rs.getString("VENDOR_NAME")));
						openDTO.setAcc_code(Formatter.nullTrim(rs.getString("CUSTOMER_UNIQUE_ID")));
						openDTO.setSlip_no(Formatter.nullTrim(rs.getString("INVOICE_NUMBER")));
						openDTO.setGl_account(glAcd);
						openDTO.setRemark(Formatter.nullTrim(rs.getString("COMMENTS")));
						openDTO.setGl_date(rs.getTimestamp("GL_DATE"));

						krw_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_KRW"))), 4);
						usd_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_USD"))), 4);
						openDTO.setExchange_rate_krw(new Double(krw_rate));
						openDTO.setExchange_rate_usd(new Double(usd_rate));
						//openDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
						openDTO.setExchange_rate_date_krw(rs.getTimestamp("EXCHANGE_RATE_DATE_KRW"));   //RYU

					} else if ("AC".equals(openType) || "AR".equals(openType)) {
						glAcd = Formatter.nullTrim(rs.getString("SEGMENT3"));
						openDTO.setItem_gubun(saOpenType(glAcd));
						openDTO.setVsl(Formatter.nullTrim(rs.getString("SEGMENT4")));
						openDTO.setVoy(Formatter.nullTrim(rs.getString("SEGMENT5")));
						openDTO.setCurrency_code(Formatter.nullTrim(rs.getString("CURRENCY_CODE")));
						openDTO.setEntered_amt(new Double(rs.getDouble("ENTERED_AMT")));
						openDTO.setUsd_amt(new Double(rs.getDouble("USD_AMT")));
						openDTO.setWon_amt(new Double(rs.getDouble("WON_AMT")));
						openDTO.setDept(Formatter.nullTrim(rs.getString("SEGMENT2")));
						openDTO.setPort(Formatter.nullTrim(rs.getString("PORT_CODE")));
						openDTO.setStl_port_code(Formatter.nullTrim(rs.getString("PORT_CODE")));
						openDTO.setAcc_name(Formatter.nullTrim(rs.getString("CUSTOMER_NAME")));
						openDTO.setAcc_code(Formatter.nullTrim(rs.getString("CUSTOMER_UNIQUE_ID")));
						openDTO.setSlip_no(Formatter.nullTrim(rs.getString("TRX_NUMBER")));
						openDTO.setGl_account(glAcd);
						openDTO.setRemark(Formatter.nullTrim(rs.getString("COMMENTS")));
						openDTO.setGl_date(rs.getTimestamp("GL_DATE"));
						krw_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_KRW"))), 4);
						usd_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_USD"))), 4);
						openDTO.setExchange_rate_krw(new Double(krw_rate));
						openDTO.setExchange_rate_usd(new Double(usd_rate));
						//openDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
						openDTO.setExchange_rate_date_krw(rs.getTimestamp("EXCHANGE_RATE_DATE_KRW"));   //RYU

					} else if ("".equals(openType)) {
						glAcd = Formatter.nullTrim(rs.getString("SEGMENT3"));
						openDTO.setItem_gubun(saOpenType(glAcd));
						openDTO.setVsl(Formatter.nullTrim(rs.getString("SEGMENT4")));
						openDTO.setVoy(Formatter.nullTrim(rs.getString("SEGMENT5")));
						openDTO.setCurrency_code(Formatter.nullTrim(rs.getString("CURRENCY_CODE")));
						openDTO.setEntered_amt(new Double(rs.getDouble("ENTERED_AMT")));
						openDTO.setUsd_amt(new Double(rs.getDouble("USD_AMT")));
						openDTO.setWon_amt(new Double(rs.getDouble("WON_AMT")));
						openDTO.setDept(Formatter.nullTrim(rs.getString("SEGMENT2")));
						openDTO.setPort(Formatter.nullTrim(rs.getString("PORT_CODE")));
						openDTO.setStl_port_code(Formatter.nullTrim(rs.getString("PORT_CODE")));
						openDTO.setAcc_name(Formatter.nullTrim(rs.getString("CUSTOMER_NAME")));
						openDTO.setAcc_code(Formatter.nullTrim(rs.getString("CUSTOMER_UNIQUE_ID")));
						openDTO.setSlip_no(Formatter.nullTrim(rs.getString("INVOICE_NUMBER")));
						openDTO.setGl_account(glAcd);
						openDTO.setRemark(Formatter.nullTrim(rs.getString("COMMENTS")));
						openDTO.setGl_date(rs.getTimestamp("GL_DATE"));

						krw_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_KRW"))), 4);
						usd_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_USD"))), 4);
						openDTO.setExchange_rate_krw(new Double(krw_rate));
						openDTO.setExchange_rate_usd(new Double(usd_rate));
						//openDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
						openDTO.setExchange_rate_date_krw(rs.getTimestamp("EXCHANGE_RATE_DATE_KRW"));  //RYU

					}
					result.add(openDTO);

				} else {

					stlDTO = new OTCSaOwnSettleDTO();
					if ("OE".equals(openType) || "RB".equals(openType) || "AP".equals(openType)) {
						stlDTO.setCheck_item("0");
						glAcd = Formatter.nullTrim(rs.getString("SEGMENT3"));
						stlDTO.setStl_vsl_code(Formatter.nullTrim(rs.getString("SEGMENT4")));
						stlDTO.setStl_voy_no(Long.valueOf(Formatter.nullTrim(rs.getString("SEGMENT5"))));
						stlDTO.setCurrency_code(Formatter.nullTrim(rs.getString("CURRENCY_CODE")));
						stlDTO.setEntered_amt(new Double(rs.getDouble("ENTERED_AMT")));
						stlDTO.setUsd_amt(new Double(rs.getDouble("USD_AMT")));
						stlDTO.setUsd_sa_amt(new Double(rs.getDouble("USD_AMT")));
						stlDTO.setWon_amt(new Double(rs.getDouble("WON_AMT")));
						stlDTO.setOp_team_code(Formatter.nullTrim(rs.getString("SEGMENT2")));
						stlDTO.setStl_port_code(Formatter.nullTrim(rs.getString("PORT_CODE")));
						stlDTO.setStl_acc_name(Formatter.nullTrim(rs.getString("VENDOR_NAME")));
						stlDTO.setStl_acc_code(Formatter.nullTrim(rs.getString("CUSTOMER_UNIQUE_ID")));
						stlDTO.setSlip_no(Formatter.nullTrim(rs.getString("INVOICE_NUMBER")));
						stlDTO.setGl_acct(glAcd);
						stlDTO.setRemark(Formatter.nullTrim(rs.getString("COMMENTS")));
						stlDTO.setGl_date(rs.getTimestamp("GL_DATE"));

						krw_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_KRW"))), 4);
						usd_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_USD"))), 4);
						stlDTO.setExchange_rate_krw(new Double(krw_rate));
						stlDTO.setExchange_rate_usd(new Double(usd_rate));
						//stlDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
						stlDTO.setExchange_rate_date_krw(rs.getTimestamp("EXCHANGE_RATE_DATE_KRW"));

					} else if ("AC".equals(openType) || "AR".equals(openType)) {
						stlDTO.setCheck_item("0");
						glAcd = Formatter.nullTrim(rs.getString("SEGMENT3"));
						stlDTO.setStl_vsl_code(Formatter.nullTrim(rs.getString("SEGMENT4")));
						stlDTO.setStl_voy_no(Long.valueOf(Formatter.nullTrim(rs.getString("SEGMENT5"))));
						stlDTO.setCurrency_code(Formatter.nullTrim(rs.getString("CURRENCY_CODE")));
						stlDTO.setEntered_amt(new Double(rs.getDouble("ENTERED_AMT")));
						stlDTO.setUsd_amt(new Double(rs.getDouble("USD_AMT")));
						stlDTO.setUsd_sa_amt(new Double(rs.getDouble("USD_AMT")));
						stlDTO.setWon_amt(new Double(rs.getDouble("WON_AMT")));
						stlDTO.setOp_team_code(Formatter.nullTrim(rs.getString("SEGMENT2")));
						stlDTO.setStl_port_code(Formatter.nullTrim(rs.getString("PORT_CODE")));
						stlDTO.setStl_acc_name(Formatter.nullTrim(rs.getString("CUSTOMER_NAME")));
						stlDTO.setStl_acc_code(Formatter.nullTrim(rs.getString("CUSTOMER_UNIQUE_ID")));
						stlDTO.setSlip_no(Formatter.nullTrim(rs.getString("TRX_NUMBER")));
						stlDTO.setGl_acct(glAcd);
						stlDTO.setRemark(Formatter.nullTrim(rs.getString("COMMENTS")));
						stlDTO.setGl_date(rs.getTimestamp("GL_DATE"));
						krw_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_KRW"))), 4);
						usd_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_USD"))), 4);
						stlDTO.setExchange_rate_krw(new Double(krw_rate));
						stlDTO.setExchange_rate_usd(new Double(usd_rate));
						//stlDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
						stlDTO.setExchange_rate_date_krw(rs.getTimestamp("EXCHANGE_RATE_DATE_KRW"));    //RYU 2010.07.14

					} else if ("".equals(openType)) {
						glAcd = Formatter.nullTrim(rs.getString("SEGMENT3"));
						stlDTO.setCheck_item("0");
						stlDTO.setStl_vsl_code(Formatter.nullTrim(rs.getString("SEGMENT4")));
						stlDTO.setStl_voy_no(Long.valueOf(Formatter.nullTrim(rs.getString("SEGMENT5"))));
						stlDTO.setCurrency_code(Formatter.nullTrim(rs.getString("CURRENCY_CODE")));
						stlDTO.setEntered_amt(new Double(rs.getDouble("ENTERED_AMT")));
						stlDTO.setUsd_amt(new Double(rs.getDouble("USD_AMT")));
						stlDTO.setWon_amt(new Double(rs.getDouble("WON_AMT")));
						stlDTO.setOp_team_code(Formatter.nullTrim(rs.getString("SEGMENT2")));
						stlDTO.setStl_port_code(Formatter.nullTrim(rs.getString("PORT_CODE")));
						stlDTO.setStl_acc_name(Formatter.nullTrim(rs.getString("CUSTOMER_NAME")));
						stlDTO.setStl_acc_code(Formatter.nullTrim(rs.getString("CUSTOMER_UNIQUE_ID")));
						stlDTO.setSlip_no(Formatter.nullTrim(rs.getString("INVOICE_NUMBER")));
						stlDTO.setGl_acct(glAcd);
						stlDTO.setRemark(Formatter.nullTrim(rs.getString("COMMENTS")));
						stlDTO.setGl_date(rs.getTimestamp("GL_DATE"));
						krw_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_KRW"))), 4);
						usd_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_USD"))), 4);
						stlDTO.setExchange_rate_krw(new Double(krw_rate));
						stlDTO.setExchange_rate_usd(new Double(usd_rate));
						//stlDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
						stlDTO.setExchange_rate_date_krw(rs.getTimestamp("EXCHANGE_RATE_DATE_KRW")); // RYU 2010.07.14

					}
					result.add(stlDTO);

				}

			}



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;

	}

	/**
	 * <p>
	 * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 Advanced Received정보을 읽어온다.
	 */
	public Collection saAdvancedReceiveSearch(String fromDate, String toDate, String accCode, Connection conn) throws Exception, STXException {

		Collection result = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;



		try {

			StringBuffer sb = new StringBuffer();

			sb.append(" select  ");
			sb.append("			  	        a.receipt_id,  ");
			sb.append("						a.receipt_number  as invoice_number,  ");
			sb.append("						a.receipt_book_number,      ");
			sb.append("						a.gl_date,                  ");
			sb.append("						a.currency_code,           ");
			sb.append("						a.entered_receipt_amount,  ");
			sb.append("						a.entered_balance_amount,  ");
			sb.append("						a.entered_pending_amount,  ");
			sb.append("						a.krw_receipt_amount,    ");
			sb.append("						a.krw_balance_amount,    ");
			sb.append("						a.krw_pending_amount,    ");
			sb.append("						a.usd_receipt_amount,    ");
			sb.append("						a.paymnet_user,          ");
			sb.append("						a.comments,                       ");
			sb.append("						a.created_user,                  ");
			sb.append("						a.customer_unique_id,            ");
			sb.append("						a.customer_number,               ");
			sb.append("						a.customer_name as vendor_name,  ");
			sb.append("						a.country,                 ");
			sb.append("						a.payment_method,          ");
			sb.append("						a.vessel_code as segment4, ");
			sb.append("						a.voyage_no as segment5,   ");
			sb.append("						a.exchange_rate_type_krw, ");
			sb.append("						a.exchange_rate_date_krw, ");
			sb.append("						a.exchange_rate_krw,      ");
			sb.append("						a.exchange_rate_type_usd, ");
			sb.append("						a.exchange_rate_date_usd, ");
			sb.append("						a.exchange_rate_usd,   ");
			sb.append("						'' as segment2,       ");
			sb.append("						'' as port_code,      ");
			sb.append("						'210701' as segment3, ");
			sb.append("	         (nvl(a.entered_balance_amount,0)-  nvl(a.entered_pending_amount,0)) as entered_amt,  ");
			sb.append("				   (nvl(a.usd_balance_amount,0)- nvl(a.usd_pending_amount,0)) as usd_amt,  ");
			sb.append("				   (nvl(a.krw_balance_amount,0)- nvl(a.krw_pending_amount,0)) as won_amt  ");
			sb.append("	  from  ear_if_receipt_balance_v a  ");
			sb.append(" where  ");
			sb.append(" 1=1  ");

			if (!"".equals(Formatter.nullTrim(fromDate))) {
				sb.append(" and a.gl_date between to_date('" + Formatter.nullTrim(fromDate) + "', 'yyyy-mm-dd') and to_date('" + Formatter.nullTrim(toDate) + "', 'yyyy-mm-dd') ");
			}

			if (!"".equals(Formatter.nullTrim(accCode))) {
				sb.append(" and  a.customer_unique_id = '" + Formatter.nullTrim(accCode) + "' ");
			}

			sb.append(" and  ( (nvl(a.entered_balance_amount,0)-  nvl(a.entered_pending_amount,0))  >  0 OR  (nvl(a.entered_balance_amount,0)-  nvl(a.entered_pending_amount,0))  <  0 )");


log.debug(">> saAdvancedReceiveSearch  :  \n " + sb.toString() );


			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaAdvancedDTO advDTO = null;
			double krw_rate = 0;
			double usd_rate = 0;
			while (rs.next()) {
				advDTO = new OTCSaAdvancedDTO();
				advDTO.setVsl(Formatter.nullTrim(rs.getString("SEGMENT4")));
				advDTO.setVoy(Formatter.nullTrim(rs.getString("SEGMENT5")));
				advDTO.setCurrency_code(Formatter.nullTrim(rs.getString("CURRENCY_CODE")));
				advDTO.setEntered_amt(new Double(rs.getDouble("ENTERED_AMT")));
				advDTO.setUsd_amt(new Double(rs.getDouble("USD_AMT")));
				advDTO.setWon_amt(new Double(rs.getLong("WON_AMT")));
				advDTO.setDept(Formatter.nullTrim(rs.getString("SEGMENT2")));
				advDTO.setPort(Formatter.nullTrim(rs.getString("PORT_CODE")));
				advDTO.setAcc_name(Formatter.nullTrim(rs.getString("VENDOR_NAME")));
				advDTO.setAcc_code(Formatter.nullTrim(rs.getString("CUSTOMER_UNIQUE_ID")));
				advDTO.setSlip_no(Formatter.nullTrim(rs.getString("INVOICE_NUMBER")));
				advDTO.setGl_account(Formatter.nullTrim(rs.getString("SEGMENT3")));
				advDTO.setRemark(Formatter.nullTrim(rs.getString("COMMENTS")));
				advDTO.setGl_date(rs.getTimestamp("GL_DATE"));

				krw_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_KRW"))), 4);
				usd_rate = Formatter.round(Formatter.nullDouble(new Double(rs.getDouble("EXCHANGE_RATE_USD"))), 4);
				advDTO.setExchange_rate_krw(new Double(krw_rate));
				advDTO.setExchange_rate_usd(new Double(usd_rate));

				//advDTO.setExchange_rate_date_krw(Formatter.nullTrim(rs.getString("EXCHANGE_RATE_DATE_KRW")));
				advDTO.setExchange_rate_date_krw(rs.getTimestamp("EXCHANGE_RATE_DATE_KRW"));  //RYU

				result.add(advDTO);

			}



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;

	}

	/**
	 * <p>
	 * 설명: 입력조건에 해당하는 sa 정보의 Balance check 정보를 읽어온다 chtInOutCOde : O, T, R
	 */
	public Collection saBalanceCheckSearch(String vslCode, Long voyNo, String chtInOutCode, Long stepNo, String cntr_no, UserBean userBean, Connection conn) throws Exception, STXException, RemoteException {

		Collection result = new ArrayList();
		Collection drs = new ArrayList();
		Collection crs = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs3 = null;
		PreparedStatement ps4 = null;
		ResultSet rs4 = null;
		PreparedStatement ps5 = null;
		ResultSet rs5 = null;
		PreparedStatement ps6 = null;
		ResultSet rs6 = null;
		PreparedStatement ps7 = null;
		ResultSet rs7 = null;
		PreparedStatement ps8 = null;
		ResultSet rs8 = null;
		PreparedStatement ps9 = null;
		ResultSet rs9 = null;
		PreparedStatement ps10 = null;
		ResultSet rs10 = null;
		PreparedStatement ps11 = null;
		ResultSet rs11 = null;
		// long bank_acc_id = 0;
		// String bank_acc_desc = "";

		long sa_no = 0;
		try {

			//String crItem = "		 select  b.trsact_code,  c.trsact_name ,b.vat_flag, ";
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
			String crItem = "	select  DECODE(b.trsact_code, 'A006','A001','A007','A002', 'A008','A004','A009','A005', 'H009','H001','H010','H002', b.trsact_code ) trsact_code,  ";
			crItem = crItem + "	   c.trsact_name ,b.vat_flag, ";

			crItem = crItem + "	   sum(nvl(b.usd_sa_amt,0)) as usd_sa_amt, sum(nvl(b.krw_sa_amt,0)) as krw_sa_amt, sum(nvl(b.usd_vat_sa_amt,0)) as usd_vat_sa_amt, sum(nvl(b.krw_vat_sa_amt,0)) as krw_vat_sa_amt ";
			crItem = crItem + "	from  otc_sa_detail b, ccd_trsact_type_m c ";
			crItem = crItem + "	where b.sa_no = ? ";
			crItem = crItem + "	and b.trsact_code = c.trsact_code  ";
			crItem = crItem + "	and c.o_sa_rpt_debit_credit = '2'  ";
			crItem = crItem + "	and c.som_system_type ='SOMO'      ";
			crItem = crItem + "	and c.own_vsl_category = ?         ";
			// WithHoldingTaxInvoice 추가 : 소득세(M001), 주민세(M002)
			//crItem = crItem + "	and ((b.trsact_code between 'A001' and 'H008') OR b.trsact_code in ('M001','M002')) ";
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
			crItem = crItem + "	and ((b.trsact_code between 'A001' and 'H011') OR b.trsact_code in ('M001','M002')) ";
			crItem = crItem + "	group by  b.trsact_code,  c.trsact_name, b.vat_flag  ";
			//crItem = crItem + "	order by b.trsact_code ";
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
			//ITEM 나오는 순서 조정..
			crItem = crItem + "		order by  DECODE(b.trsact_code, 'A006','A001','A007','A002', 'A008','A004','A009','A005', 'H009','H001','H010','H002', b.trsact_code ) ";
log.debug("saBalanceCheckSearch - crItem 쿼리 : \n" + crItem);


			//String crItemAC = " select   b.trsact_code,  c.trsact_name ,b.vat_flag, b.remark,";
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
			String crItemAC = "	select  DECODE(b.trsact_code, 'I071','I003','I072','I004','I072','I005', b.trsact_code ) trsact_code,  ";
			crItemAC = crItemAC + "  c.trsact_name ,b.vat_flag, b.remark,";

			crItemAC = crItemAC + "	sum(nvl(b.usd_sa_amt,0)) as usd_sa_amt, sum(nvl(b.krw_sa_amt,0)) as krw_sa_amt, sum(nvl(b.usd_vat_sa_amt,0)) as usd_vat_sa_amt, sum(nvl(b.krw_vat_sa_amt,0)) as krw_vat_sa_amt ";
			crItemAC = crItemAC + "	from otc_sa_detail b, ccd_trsact_type_m c ";
			crItemAC = crItemAC + "	where  b.sa_no = ? ";
			crItemAC = crItemAC + "	and b.trsact_code = c.trsact_code  ";
			crItemAC = crItemAC + "	and c.o_sa_rpt_debit_credit = '2'  ";
			crItemAC = crItemAC + "	and c.som_system_type ='SOMO'      ";
			crItemAC = crItemAC + "	and c.own_vsl_category = ?         ";
			//crItemAC = crItemAC + "	and  b.trsact_code IN ('I001', 'I002','I003','I004','I005','I006','I007', 'I008','I009','I010','I011','I012','I013','I014','I015','I016','I017','I018','I019','I020','I021','I022','I023','I024','I025','I026','I027','I028','I029','I030','I031','I032','I033','I034','I035','I036','I037','I038','I039','I040','I041','I042','I043','I044','I045','I046','I047','I048','I049','I050','I051','I052','I053','I054','J001','J002','J003','J004','J005','J006','J007','J008','J009','J010','K001','K002','K003','K004','K005','K006','M005','M006','M007','M008','N001','N002') ";
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
			crItemAC = crItemAC + "	and  b.trsact_code IN ('I001', 'I002', 'I003','I004','I005','I071','I072','I073', 'I006','I007', 'I008','I009','I010','I011','I012','I013','I014','I015','I016','I017','I018','I019','I020','I021','I022','I023','I024','I025','I026','I027','I028','I029','I030','I031','I032','I033','I034','I035','I036','I037','I038','I039','I040','I041','I042','I043','I044','I045','I046','I047','I048','I049','I050','I051','I052','I053','I054','J001','J002','J003','J004','J005','J006','J007','J008','J009','J010','K001','K002','K003','K004','K005','K006','K007','M005','M006','M007','M008','N001','N002', 'I074','I075','J008','J009','J011','J012') ";	/// K007 추가 (hijang 20160906 ) - 신규 거래유형(I074,I075,J008,J009) 추가 20150422 hijang, 신규 거래유형(J011) 추가 240206, J012추가 250212
			crItemAC = crItemAC + "	group by b.trsact_code,  c.trsact_name, b.vat_flag ,b.remark";
			//crItemAC = crItemAC + "	order by b.trsact_code";
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
			//ITEM 나오는 순서 조정..
			crItemAC = crItemAC + "	order by DECODE(b.trsact_code, 'I071','I003','I072','I004','I072','I005', b.trsact_code )";
log.debug("saBalanceCheckSearch - crItemAC 쿼리 : \n" + crItemAC);

			String crItemAF = "		 select   b.trsact_code,  c.trsact_name ,b.vat_flag,";
			crItemAF = crItemAF + "	sum(nvl(b.usd_sa_amt,0)) as usd_sa_amt, sum(nvl(b.krw_sa_amt,0)) as krw_sa_amt, sum(nvl(b.usd_vat_sa_amt,0)) as usd_vat_sa_amt, sum(nvl(b.krw_vat_sa_amt,0)) as krw_vat_sa_amt ";
			crItemAF = crItemAF + "	from  otc_sa_detail b, ccd_trsact_type_m c ";
			crItemAF = crItemAF + "	where  b.sa_no  = ? ";
			crItemAF = crItemAF + "	and b.trsact_code = c.trsact_code  ";
			crItemAF = crItemAF + "	and c.o_sa_rpt_debit_credit = '2'  ";
			crItemAF = crItemAF + "	and c.som_system_type ='SOMO'      ";
			crItemAF = crItemAF + "	and c.own_vsl_category = ?         ";
			crItemAF = crItemAF + "	and b.trsact_code  >  'N002' ";
			crItemAF = crItemAF + "	group by  b.trsact_code,  c.trsact_name, b.vat_flag  ";
			crItemAF = crItemAF + "	order by b.trsact_code ";

			String crTot = "	select b.sa_no, sum(nvl(b.usd_sa_amt,0)) + sum(nvl(b.usd_vat_sa_amt,0)) as usd_amt, sum(nvl(b.krw_sa_amt,0))+ sum(nvl(b.krw_vat_sa_amt,0)) as krw_amt    ";
			crTot = crTot + "			from  otc_sa_detail b, ccd_trsact_type_m c    ";
			crTot = crTot + "				where b.sa_no = ?   ";
			crTot = crTot + "				and b.trsact_code = c.trsact_code    ";
			crTot = crTot + "				and c.o_sa_rpt_debit_credit = '2'    ";
			crTot = crTot + "				and c.som_system_type ='SOMO'    ";
			crTot = crTot + "				and c.own_vsl_category = ?    ";
			crTot = crTot + "				and b.trsact_code not in ( 'L001' , 'L002')    ";
			crTot = crTot + "				group by b.sa_no    ";

			String crBal = "		  select b.due_date, b.pymt_term, b.terms_date, b.pymt_hold_flag, b.pymt_meth,nvl(b.bank_acc_id,0) as bank_acc_id, b.bank_acc_desc,";
			crBal = crBal + "	            nvl(b.usd_sa_amt,0) + nvl(b.usd_vat_sa_amt,0) as usd_amt, nvl(b.krw_sa_amt,0)+ nvl(b.krw_vat_sa_amt,0) as krw_amt,  ";
			crBal = crBal + "               court_flag, court_admit_no ";	//140314 GYJ 법원허가번호 추가
			crBal = crBal + "				from  otc_sa_detail b, ccd_trsact_type_m c       ";
			crBal = crBal + "				where  b.sa_no  = ?                                        ";
			crBal = crBal + "				and b.trsact_code = c.trsact_code                               ";
			crBal = crBal + "				and c.o_sa_rpt_debit_credit = '2'                               ";
			crBal = crBal + "				and c.own_vsl_category = ?                                      ";
			crBal = crBal + "				and c.som_system_type ='SOMO'                                   ";
			crBal = crBal + "				and b.trsact_code ='L001'                                       ";

			//String drItem = "			 select 	b.trsact_code,  c.trsact_name ,b.vat_flag, ";
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
			String drItem = "			select  DECODE(b.trsact_code, 'A006','A001','A007','A002', 'A008','A004','A009','A005', 'H009','H001','H010','H002', b.trsact_code ) trsact_code,  ";
			drItem = drItem + "			 	c.trsact_name ,b.vat_flag, ";
			drItem = drItem + "		 		sum(nvl(b.usd_sa_amt,0)) as usd_sa_amt , sum(nvl(b.krw_sa_amt,0)) as krw_sa_amt, sum(nvl(b.usd_vat_sa_amt,0)) as usd_vat_sa_amt, sum(nvl(b.krw_vat_sa_amt,0)) as krw_vat_sa_amt ";
			drItem = drItem + "		 		from  otc_sa_detail b, ccd_trsact_type_m c ";
			drItem = drItem + "		 		where  b.sa_no = ? ";
			drItem = drItem + "		 		and b.trsact_code = c.trsact_code  ";
			drItem = drItem + "		 		and c.o_sa_rpt_debit_credit = '1'  ";
			drItem = drItem + "		 		and c.som_system_type ='SOMO'      ";
			drItem = drItem + "		 		and c.own_vsl_category = ?         ";
			// WithHoldingTaxInvoice 추가 : M004(소득세+주민세=지급운임기타)
			//drItem = drItem + "		 		and ((b.trsact_code between 'A001' and 'H008') OR b.trsact_code = 'M004') ";
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
			drItem = drItem + "		 		and ((b.trsact_code between 'A001' and 'H011') OR b.trsact_code = 'M004') ";
			drItem = drItem + "		 		group by b.trsact_code,  c.trsact_name, b.vat_flag ";
			///drItem = drItem + "	            order by b.trsact_code ";
			// 결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20150112
			// ITEM 나오는 순서 조정..
			drItem = drItem + "	            order by DECODE(b.trsact_code, 'A006','A001','A007','A002', 'A008','A004','A009','A005', 'H009','H001','H010','H002', b.trsact_code ) ";
log.debug("saBalanceCheckSearch - drItem 쿼리 : \n" + drItem);

			//String drItemAC = "			 select  b.trsact_code,  c.trsact_name ,b.vat_flag, b.remark, ";
			String drItemAC = "			 select DECODE(b.trsact_code, 'I071','I003','I072','I004','I072','I005', b.trsact_code ) trsact_code,  ";
			drItemAC = drItemAC + "				c.trsact_name ,b.vat_flag, b.remark, ";

			drItemAC = drItemAC + "		 		sum(nvl(b.usd_sa_amt,0)) as usd_sa_amt , sum(nvl(b.krw_sa_amt,0)) as krw_sa_amt, sum(nvl(b.usd_vat_sa_amt,0)) as usd_vat_sa_amt, sum(nvl(b.krw_vat_sa_amt,0)) as krw_vat_sa_amt ";
			drItemAC = drItemAC + "		 		from otc_sa_detail b, ccd_trsact_type_m c ";
			drItemAC = drItemAC + "		 		where  b.sa_no = ? ";
			drItemAC = drItemAC + "		 		and b.trsact_code = c.trsact_code  ";
			drItemAC = drItemAC + "		 		and c.o_sa_rpt_debit_credit = '1'  ";
			drItemAC = drItemAC + "		 		and c.som_system_type ='SOMO'      ";
			drItemAC = drItemAC + "		 		and c.own_vsl_category = ?         ";
			//drItemAC = drItemAC + "		 		and  b.trsact_code IN ('I001', 'I002','I003','I004','I005','I006','I007', 'I008','I009','I010','I011','I012','I013','I014','I015','I016','I017','I018','I019','I020','I021','I022','I023','I024','I025','I026','I027','I028','I029','I030','I031','I032','I033','I034','I035','I036','I037','I038','I039','I040','I041','I042','I043','I044','I045','I046','I047','I048','I049','I050','I051','I052','I053','I054','J001','J002','J003','J004','J005','J006','J007','J008','J009','J010','K001','K002','K003','K004','K005','K006','M005','M006','M007','M008','N001','N002') ";
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
			drItemAC = drItemAC + "		 		and  b.trsact_code IN ('I001','I002', 'I003','I004','I005','I071','I072','I073', 'I006','I007', 'I008','I009','I010','I011','I012','I013','I014','I015','I016','I017','I018','I019','I020','I021','I022','I023','I024','I025','I026','I027','I028','I029','I030','I031','I032','I033','I034','I035','I036','I037','I038','I039','I040','I041','I042','I043','I044','I045','I046','I047','I048','I049','I050','I051','I052','I053','I054','J001','J002','J003','J004','J005','J006','J007','J008','J009','J010','K001','K002','K003','K004','K005','K006','K007','M005','M006','M007','M008','N001','N002', 'I074','I075','J008','J009','J011','J012') ";	/// K007 추가 (hijang 20160906 ) - 신규 거래유형(I074,I075,J008,J009) 추가 20150422 hijang - 신규 J011 추가 240206, J012추가 250212
			drItemAC = drItemAC + "		 		group by b.trsact_code,  c.trsact_name, b.vat_flag ,b.remark ";
			//drItemAC = drItemAC + "	            order by b.trsact_code ";
			// 결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
			// ITEM 나오는 순서 조정..
			drItemAC = drItemAC + "	            order by DECODE(b.trsact_code, 'I071','I003','I072','I004','I072','I005', b.trsact_code ) ";
log.debug("saBalanceCheckSearch - drItemAC 쿼리 : \n" + drItemAC);

			String drItemAF = "			 select b.trsact_code,  c.trsact_name ,b.vat_flag,  ";
			drItemAF = drItemAF + "		 		 sum(nvl(b.usd_sa_amt,0)) as usd_sa_amt , sum(nvl(b.krw_sa_amt,0)) as krw_sa_amt, sum(nvl(b.usd_vat_sa_amt,0)) as usd_vat_sa_amt, sum(nvl(b.krw_vat_sa_amt,0)) as krw_vat_sa_amt ";
			drItemAF = drItemAF + "		 		from otc_sa_detail b, ccd_trsact_type_m c ";
			drItemAF = drItemAF + "		 		where  b.sa_no = ? ";
			drItemAF = drItemAF + "		 		and b.trsact_code = c.trsact_code  ";
			drItemAF = drItemAF + "		 		and c.o_sa_rpt_debit_credit = '1'  ";
			drItemAF = drItemAF + "		 		and c.som_system_type ='SOMO'      ";
			drItemAF = drItemAF + "		 		and c.own_vsl_category = ?         ";
			drItemAF = drItemAF + "		 		and b.trsact_code  >  'N002' ";
			drItemAF = drItemAF + "		 		group by  b.trsact_code,  c.trsact_name, b.vat_flag ";
			drItemAF = drItemAF + "	            order by b.trsact_code ";

			String drTot = "				 select b.sa_no, sum(nvl(b.usd_sa_amt,0)) + sum(nvl(b.usd_vat_sa_amt,0)) as usd_amt, sum(nvl(b.krw_sa_amt,0))+ sum(nvl(b.krw_vat_sa_amt,0)) as krw_amt  ";
			drTot = drTot + "					from  otc_sa_detail b, ccd_trsact_type_m c   ";
			drTot = drTot + "					where  b.sa_no = ?  ";
			drTot = drTot + "					and b.trsact_code = c.trsact_code   ";
			drTot = drTot + "					and c.o_sa_rpt_debit_credit = '1'    ";
			drTot = drTot + "					and c.som_system_type ='SOMO'         ";
			drTot = drTot + "					and c.own_vsl_category = ?             ";
			drTot = drTot + "					and b.trsact_code not in ( 'L001' , 'L002')   ";
			drTot = drTot + "					group by b.sa_no   ";

			String drBal = "		        select b.due_date, b.pymt_term, b.terms_date, b.pymt_hold_flag, b.pymt_meth,nvl(b.bank_acc_id,0) as bank_acc_id, b.bank_acc_desc,";
			drBal = drBal + "				 nvl(b.usd_sa_amt,0) + nvl(b.usd_vat_sa_amt,0) as usd_amt, nvl(b.krw_sa_amt,0)+ nvl(b.krw_vat_sa_amt,0) as krw_amt ";
			drBal = drBal + "					from  otc_sa_detail b, ccd_trsact_type_m c ";
			drBal = drBal + "					where  ";
			drBal = drBal + "					b.sa_no = ?     ";
			drBal = drBal + "					and b.trsact_code = c.trsact_code    ";
			drBal = drBal + "					and c.o_sa_rpt_debit_credit = '1'    ";
			drBal = drBal + "					and c.own_vsl_category = ?           ";
			drBal = drBal + "					and c.som_system_type ='SOMO'        ";
			drBal = drBal + "					and b.trsact_code = 'L002'           ";

			String saHead = "			 select  a.sa_no, a.cht_in_out_code,COMCODE_INFO_FUNC('CHT_IN_OUT_CODE', a.cht_in_out_code) as cht_in_out_name, 		";
			saHead = saHead + " 				 a.voy_no, a.step_no, a.cntr_no,a.posting_date, a.op_team_code,team_info_func(a.op_team_code) as team_name, 	";
			saHead = saHead + "		         a.cntr_acc_code,ACC_NAME_FUNC(A.CNTR_ACC_CODE) AS acc_name, ACC_NATION_FUNC(A.CNTR_ACC_CODE) AS nat_code,  	";
			saHead = saHead + "		 		 a.vsl_code , vsl_name_func(a.vsl_code) as vsl_name, a.process_sts_flag,										";
			saHead = saHead + "				 nat_eng_name_func(acc_nation_func(a.cntr_acc_code)) as nat_name, a.loc_exc_rate, a.wth_flag, 					"; //wth_tax 추가
			//saHead = saHead + "					 ( select income_rate from otc_wth_tax_m where nat_code = acc_nation_func(a.cntr_acc_code) ) income_tax_rate,	"; //wth_tax 추가
			// 수정,,(bbc 선박 ) - hijang
			//saHead = saHead + "                  nvl( (select income_rate from OTC_WITHOLD_VSL_INFO where vsl_code = a.vsl_code ),    	";
			//saHead = saHead + "                         (select income_rate from otc_wth_tax_m where nat_code = acc_nation_func(a.cntr_acc_code)) ) income_tax_rate,	  	";
			// 수정,,(bbc 선박 ) - hijang(2012.04.19)
			//saHead = saHead + "                GET_WTH_TAX_RATE(a.cntr_acc_code, a.wth_flag) as income_tax_rate,	  	";
			saHead = saHead + "                GET_WTH_TAX_RATE(a.cntr_acc_code, a.cntr_no ) as income_tax_rate,	  	";
			saHead = saHead + " 				 a.exc_date, a.exc_rate_type, a.usd_exc_rate, a.usd_loc_rate, a.curcy_code, a.cancel_flag, 						"; //wth_tax 추가
			saHead = saHead + "                (select b.benef_acc_code from otc_cp_item_head b where a.cntr_no = b.cntr_no) benef_Acc_Code         ";	//bank acc code를 beneficiary acc code로 조회하게끔 수정 150519 GYJ
			saHead = saHead + "				from otc_sa_head a      ";
			saHead = saHead + "	                where a.vsl_code = ?     ";
			saHead = saHead + "					and a.voy_no =?        ";
			saHead = saHead + "					and a.cht_in_out_code = ?  ";
			saHead = saHead + "					and a.step_no =?           ";
log.debug("saBalanceCheckSearch - saHead 쿼리 : \n" + saHead);

			// debit의 item을 구함 ============================================
			OTCBalanceCheckDTO balDTO = null;
			OTCBalanceHeadDTO bhDTO = new OTCBalanceHeadDTO();

			log.debug("saHead : " + saHead) ;

			ps11 = conn.prepareStatement(saHead);
			int j2 = 1;
			ps11.setString(j2++, vslCode);
			ps11.setLong(j2++, voyNo.longValue());
			ps11.setString(j2++, chtInOutCode);
			ps11.setLong(j2++, stepNo.longValue());

			rs11 = ps11.executeQuery();
			String io = "";
			while (rs11.next()) {
				sa_no = rs11.getLong("sa_no");
				bhDTO.setSa_no(new Long(rs11.getLong("sa_no")));
				bhDTO.setOp_team_code(rs11.getString("op_team_code"));
				bhDTO.setOp_team_name(rs11.getString("team_name"));
				bhDTO.setCntr_acc_code(rs11.getString("cntr_acc_code"));
				bhDTO.setCntr_acc_name(rs11.getString("acc_name"));
				bhDTO.setNat_code(rs11.getString("nat_code"));
				bhDTO.setVsl_code(rs11.getString("vsl_code"));
				bhDTO.setVsl_name(rs11.getString("vsl_name"));
				bhDTO.setProcess_sts_flag(Formatter.nullTrim(rs11.getString("process_sts_flag")));
				bhDTO.setPosting_date(rs11.getTimestamp("posting_date"));
				io = rs11.getString("cht_in_out_code");
				if ("T".equals(io)) {
					bhDTO.setCht_in_out_name("T/C IN");
				} else {
					bhDTO.setCht_in_out_name("T/C OUT");
				}
				bhDTO.setCht_in_out_code(rs11.getString("cht_in_out_code"));

				bhDTO.setVoy_no(new Long(rs11.getLong("voy_no")));
				bhDTO.setStep_no(new Long(rs11.getLong("step_no")));
				bhDTO.setCntr_no(rs11.getString("cntr_no"));

				//용선 원천 징수에 관련된 param 추가
				bhDTO.setNat_name(rs11.getString("nat_name"));
				bhDTO.setLoc_exc_rate(new Double(rs11.getDouble("loc_exc_rate")));
				bhDTO.setWth_flag(rs11.getString("wth_flag"));
				bhDTO.setIncome_tax_rate(new Double(rs11.getDouble("income_tax_rate")));
				bhDTO.setExc_date(rs11.getTimestamp("exc_date"));
				bhDTO.setExc_rate_type(rs11.getString("exc_rate_type"));
				bhDTO.setUsd_exc_rate(new Double(rs11.getDouble("usd_exc_rate")));
				bhDTO.setUsd_loc_rate(new Double(rs11.getDouble("usd_loc_rate")));
				bhDTO.setCurcy_code(rs11.getString("curcy_code"));
				bhDTO.setCancel_flag(rs11.getString("cancel_flag"));

				//150519 GYJ
				bhDTO.setBenef_acc_code(rs11.getString("benef_acc_code"));

			}
			ps4 = conn.prepareStatement(drItem);

			int i = 1;

			ps4.setLong(i++, sa_no);
			ps4.setString(i++, chtInOutCode);

			rs4 = ps4.executeQuery();
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

			Collection debit = new ArrayList();

			while (rs4.next()) {
				balDTO = new OTCBalanceCheckDTO();

				vat_flag = Formatter.nullTrim(rs4.getString("vat_flag"));
				trsact_name = Formatter.nullTrim(rs4.getString("trsact_name"));
				trsact_code = Formatter.nullTrim(rs4.getString("trsact_code"));

				if ("Y".equals(vat_flag)) {
					loc_amt = Formatter.nullDouble(new Double(rs4.getDouble("usd_sa_amt")));
					vat_loc_amt = Formatter.nullDouble(new Double(rs4.getDouble("usd_vat_sa_amt")));
					won_amt = Formatter.nullDouble(new Double(rs4.getDouble("krw_sa_amt")));
					vat_won_amt = Formatter.nullDouble(new Double(rs4.getDouble("krw_vat_sa_amt")));
				} else {
					loc_amt = Formatter.nullDouble(new Double(rs4.getDouble("usd_sa_amt")));
					vat_loc_amt = 0;
					won_amt = Formatter.nullDouble(new Double(rs4.getDouble("krw_sa_amt")));
					vat_won_amt = 0;
				}

				balDTO.setDebit_item_code(trsact_code);
				balDTO.setDebit_item(trsact_name);
				balDTO.setDebit_loc(new Double(loc_amt));
				balDTO.setDebit_won(new Double(won_amt));
				debit.add(balDTO);

// kgw 20080729 (1)
//				if ("Y".equals(vat_flag) && vat_won_amt > 0) {
				if ("Y".equals(vat_flag) ) {
					balDTO = new OTCBalanceCheckDTO();
					trsact_name = trsact_name + " " + "VAT";
					balDTO.setDebit_item_code(trsact_code);
					balDTO.setDebit_item(trsact_name);
					balDTO.setDebit_loc(new Double(vat_loc_amt));
					balDTO.setDebit_won(new Double(vat_won_amt));
					debit.add(balDTO);

				}
				row = row + 1;
			}

			ps7 = conn.prepareStatement(drItemAC);

			i = 1;

			ps7.setLong(i++, sa_no);
			ps7.setString(i++, chtInOutCode);

			rs7 = ps7.executeQuery();

			while (rs7.next()) {
				balDTO = new OTCBalanceCheckDTO();

				vat_flag = Formatter.nullTrim(rs7.getString("vat_flag"));
				trsact_name = Formatter.nullTrim(rs7.getString("trsact_name"));
				trsact_code = Formatter.nullTrim(rs7.getString("trsact_code"));

				if ("Y".equals(vat_flag)) {
					loc_amt = Formatter.nullDouble(new Double(rs7.getDouble("usd_sa_amt")));
					vat_loc_amt = Formatter.nullDouble(new Double(rs7.getDouble("usd_vat_sa_amt")));
					won_amt = Formatter.nullDouble(new Double(rs7.getDouble("krw_sa_amt")));
					vat_won_amt = Formatter.nullDouble(new Double(rs7.getDouble("krw_vat_sa_amt")));
				} else {
					loc_amt = Formatter.nullDouble(new Double(rs7.getDouble("usd_sa_amt")));
					vat_loc_amt = 0;
					won_amt = Formatter.nullDouble(new Double(rs7.getDouble("krw_sa_amt")));
					vat_won_amt = 0;
				}

				balDTO.setDebit_item_code(trsact_code);
				if (!"".equals(Formatter.nullTrim(rs7.getString("remark")))) {
					balDTO.setDebit_item(trsact_name.concat("  :  ").concat(Formatter.nullTrim(rs7.getString("remark"))));
				} else {
					balDTO.setDebit_item(trsact_name);
				}
				balDTO.setDebit_loc(new Double(loc_amt));
				balDTO.setDebit_won(new Double(won_amt));
				debit.add(balDTO);

//				 kgw 20080729 (2)
//				if ("Y".equals(vat_flag) && vat_won_amt > 0) {
				if ("Y".equals(vat_flag)) {
					balDTO = new OTCBalanceCheckDTO();
					trsact_name = trsact_name + " " + "VAT";
					balDTO.setDebit_item_code(trsact_code);
					balDTO.setDebit_item(trsact_name);
					balDTO.setDebit_loc(new Double(vat_loc_amt));
					balDTO.setDebit_won(new Double(vat_won_amt));
					debit.add(balDTO);

				}
				row = row + 1;
			}

			ps8 = conn.prepareStatement(drItemAF);

			i = 1;

			ps8.setLong(i++, sa_no);
			ps8.setString(i++, chtInOutCode);

			rs8 = ps8.executeQuery();

			while (rs8.next()) {
				balDTO = new OTCBalanceCheckDTO();

				vat_flag = Formatter.nullTrim(rs8.getString("vat_flag"));
				trsact_name = Formatter.nullTrim(rs8.getString("trsact_name"));
				trsact_code = Formatter.nullTrim(rs8.getString("trsact_code"));

				if ("Y".equals(vat_flag)) {
					loc_amt = Formatter.nullDouble(new Double(rs8.getDouble("usd_sa_amt")));
					vat_loc_amt = Formatter.nullDouble(new Double(rs8.getDouble("usd_vat_sa_amt")));
					won_amt = Formatter.nullDouble(new Double(rs8.getDouble("krw_sa_amt")));
					vat_won_amt = Formatter.nullDouble(new Double(rs8.getDouble("krw_vat_sa_amt")));
				} else {
					loc_amt = Formatter.nullDouble(new Double(rs8.getDouble("usd_sa_amt")));
					vat_loc_amt = 0;
					won_amt = Formatter.nullDouble(new Double(rs8.getDouble("krw_sa_amt")));
					vat_won_amt = 0;
				}

				balDTO.setDebit_item_code(trsact_code);
				balDTO.setDebit_item(trsact_name);
				balDTO.setDebit_loc(new Double(loc_amt));
				balDTO.setDebit_won(new Double(won_amt));
				debit.add(balDTO);
//				 kgw 20080729 (3)
//				if ("Y".equals(vat_flag) && vat_won_amt > 0) {
				if ("Y".equals(vat_flag)) {
					balDTO = new OTCBalanceCheckDTO();
					trsact_name = trsact_name + " " + "VAT";
					balDTO.setDebit_item_code(trsact_code);
					balDTO.setDebit_item(trsact_name);
					balDTO.setDebit_loc(new Double(vat_loc_amt));
					balDTO.setDebit_won(new Double(vat_won_amt));
					debit.add(balDTO);

				}
				row = row + 1;
			}
			drs.add(debit);

			// debit 총합을 구함 ============================================
			ps5 = conn.prepareStatement(drTot);

			int k = 1;

			ps5.setLong(k++, sa_no);
			ps5.setString(k++, chtInOutCode);

			rs5 = ps5.executeQuery();
			balDTO = null;
			while (rs5.next()) {
				balDTO = new OTCBalanceCheckDTO();
				loc_amt = Formatter.nullDouble(new Double(rs5.getDouble("usd_amt")));
				won_amt = Formatter.nullDouble(new Double(rs5.getDouble("krw_amt")));
				trsact_name = "";
				balDTO.setDebit_item(trsact_name);
				balDTO.setDebit_loc(new Double(loc_amt));
				balDTO.setDebit_won(new Double(won_amt));

				dr_tot_loc_amt = dr_tot_loc_amt + loc_amt;
log.debug("---차변 TOTAL(drTot) 값  dr_tot_loc_amt : " + dr_tot_loc_amt );
log.debug("---차변 TOTAL(drTot)  값  loc_amt : " + loc_amt );

				dr_tot_won_amt = dr_tot_won_amt + won_amt;
			}

			drs.add(balDTO);

			// debit balance 총합을 구함 ============================================
			ps6 = conn.prepareStatement(drBal);
			int j = 1;

			ps6.setLong(j++, sa_no);
			ps6.setString(j++, chtInOutCode);

			rs6 = ps6.executeQuery();
			balDTO = null;
			while (rs6.next()) {
				balDTO = new OTCBalanceCheckDTO();
				loc_amt = Formatter.nullDouble(new Double(rs6.getDouble("usd_amt")));
				won_amt = Formatter.nullDouble(new Double(rs6.getDouble("krw_amt")));
				// bank_acc_id = rs6.getLong("bank_acc_id");
				// bank_acc_desc =
				// Formatter.nullTrim(rs6.getString("bank_acc_desc"));
				trsact_name = "";
				balDTO.setDebit_item(trsact_name);
				balDTO.setDebit_loc(new Double(loc_amt));
				balDTO.setDebit_won(new Double(won_amt));

				if (loc_amt > 0) {
					bhDTO.setApAr("AR");
				}

				dr_tot_loc_amt = dr_tot_loc_amt + loc_amt;
log.debug("---차변 BALANCE값(drBal)  dr_tot_loc_amt : " + dr_tot_loc_amt );
log.debug("---차변 BALANCE값(drBal)  loc_amt : " + loc_amt );

				dr_tot_won_amt = dr_tot_won_amt + won_amt;
				bhDTO.setDue_date(rs6.getTimestamp("due_date"));
				bhDTO.setPymt_term(rs6.getString("pymt_term"));
				bhDTO.setTerms_date(rs6.getTimestamp("terms_date"));
				bhDTO.setPymt_hold_flag(rs6.getString("pymt_hold_flag"));
				bhDTO.setPymt_meth(rs6.getString("pymt_meth"));
				bhDTO.setPymt_term(rs6.getString("pymt_term"));
				bhDTO.setBank_acc_id(new Long(rs6.getLong("bank_acc_id")));
				bhDTO.setBank_acc_desc(rs6.getString("bank_acc_desc"));

			}
			drs.add(balDTO);

			balDTO = new OTCBalanceCheckDTO();

			balDTO.setDr_tot_loc_amt(new Double(dr_tot_loc_amt));
			balDTO.setDr_tot_won_amt(new Double(dr_tot_won_amt));
			drs.add(balDTO);

			// credit item을 구함 대변
			ps = conn.prepareStatement(crItem);

			int l = 1;

			ps.setLong(l++, sa_no);
			ps.setString(l++, chtInOutCode);

			rs = ps.executeQuery();
			balDTO = null;
			Collection credit = new ArrayList();
			io = "";
			while (rs.next()) {

				balDTO = new OTCBalanceCheckDTO();

				vat_flag = Formatter.nullTrim(rs.getString("vat_flag"));
				trsact_name = Formatter.nullTrim(rs.getString("trsact_name"));
				trsact_code = Formatter.nullTrim(rs.getString("trsact_code"));
				if ("Y".equals(vat_flag)) {
					loc_amt = Formatter.nullDouble(new Double(rs.getDouble("usd_sa_amt")));
					vat_loc_amt = Formatter.nullDouble(new Double(rs.getDouble("usd_vat_sa_amt")));
					won_amt = Formatter.nullDouble(new Double(rs.getDouble("krw_sa_amt")));
					vat_won_amt = Formatter.nullDouble(new Double(rs.getDouble("krw_vat_sa_amt")));
				} else {
					loc_amt = Formatter.nullDouble(new Double(rs.getDouble("usd_sa_amt")));
					vat_loc_amt = 0;
					won_amt = Formatter.nullDouble(new Double(rs.getDouble("krw_sa_amt")));
					vat_won_amt = 0;
				}

				balDTO.setCredit_item_code(trsact_code);
				balDTO.setCredit_item(trsact_name);
				balDTO.setCredit_loc(new Double(loc_amt));
				balDTO.setCredit_won(new Double(won_amt));

				credit.add(balDTO);
//				 kgw 20080729 (4)
//				if ("Y".equals(vat_flag) && vat_won_amt > 0) {
				if ("Y".equals(vat_flag)) {
					balDTO = new OTCBalanceCheckDTO();
					trsact_name = trsact_name + " " + "VAT";
					balDTO.setCredit_item_code(trsact_code);
					balDTO.setCredit_item(trsact_name);
					balDTO.setCredit_loc(new Double(vat_loc_amt));
					balDTO.setCredit_won(new Double(vat_won_amt));
					credit.add(balDTO);
				}

			}
			ps9 = conn.prepareStatement(crItemAC);
			l = 1;

			ps9.setLong(l++, sa_no);
			ps9.setString(l++, chtInOutCode);

			rs9 = ps9.executeQuery();

			io = "";
			while (rs9.next()) {

				balDTO = new OTCBalanceCheckDTO();

				vat_flag = Formatter.nullTrim(rs9.getString("vat_flag"));
				trsact_name = Formatter.nullTrim(rs9.getString("trsact_name"));
				trsact_code = Formatter.nullTrim(rs9.getString("trsact_code"));

				if ("Y".equals(vat_flag)) {
					loc_amt = Formatter.nullDouble(new Double(rs9.getDouble("usd_sa_amt")));
					vat_loc_amt = Formatter.nullDouble(new Double(rs9.getDouble("usd_vat_sa_amt")));
					won_amt = Formatter.nullDouble(new Double(rs9.getDouble("krw_sa_amt")));
					vat_won_amt = Formatter.nullDouble(new Double(rs9.getDouble("krw_vat_sa_amt")));
				} else {
					loc_amt = Formatter.nullDouble(new Double(rs9.getDouble("usd_sa_amt")));
					vat_loc_amt = 0;
					won_amt = Formatter.nullDouble(new Double(rs9.getDouble("krw_sa_amt")));
					vat_won_amt = 0;
				}

				balDTO.setCredit_item_code(trsact_code);
				if (!"".equals(Formatter.nullTrim(rs9.getString("remark")))) {
					balDTO.setCredit_item(trsact_name.concat("  :  ").concat(Formatter.nullTrim(rs9.getString("remark"))));
				} else {
					balDTO.setCredit_item(trsact_name);
				}
				balDTO.setCredit_loc(new Double(loc_amt));
				balDTO.setCredit_won(new Double(won_amt));

				credit.add(balDTO);
//				 kgw 20080729 (5)
//				if ("Y".equals(vat_flag) && vat_won_amt > 0) {
				if ("Y".equals(vat_flag)) {
					balDTO = new OTCBalanceCheckDTO();
					trsact_name = trsact_name + " " + "VAT";
					balDTO.setCredit_item_code(trsact_code);
					balDTO.setCredit_item(trsact_name);
					balDTO.setCredit_loc(new Double(vat_loc_amt));
					balDTO.setCredit_won(new Double(vat_won_amt));
					credit.add(balDTO);
				}

			}

			ps10 = conn.prepareStatement(crItemAF);
			l = 1;

			ps10.setLong(l++, sa_no);
			ps10.setString(l++, chtInOutCode);

			rs10 = ps10.executeQuery();

			io = "";
			while (rs10.next()) {

				balDTO = new OTCBalanceCheckDTO();

				vat_flag = Formatter.nullTrim(rs10.getString("vat_flag"));
				trsact_name = Formatter.nullTrim(rs10.getString("trsact_name"));
				trsact_code = Formatter.nullTrim(rs10.getString("trsact_code"));
				if ("Y".equals(vat_flag)) {
					loc_amt = Formatter.nullDouble(new Double(rs10.getDouble("usd_sa_amt")));
					vat_loc_amt = Formatter.nullDouble(new Double(rs10.getDouble("usd_vat_sa_amt")));
					won_amt = Formatter.nullDouble(new Double(rs10.getDouble("krw_sa_amt")));
					vat_won_amt = Formatter.nullDouble(new Double(rs10.getDouble("krw_vat_sa_amt")));
				} else {
					loc_amt = Formatter.nullDouble(new Double(rs10.getDouble("usd_sa_amt")));
					vat_loc_amt = 0;
					won_amt = Formatter.nullDouble(new Double(rs10.getDouble("krw_sa_amt")));
					vat_won_amt = 0;
				}

				balDTO.setCredit_item_code(trsact_code);
				balDTO.setCredit_item(trsact_name);
				balDTO.setCredit_loc(new Double(loc_amt));
				balDTO.setCredit_won(new Double(won_amt));

				credit.add(balDTO);
//				 kgw 20080729 (6)
//				if ("Y".equals(vat_flag) && vat_won_amt > 0) {
				if ("Y".equals(vat_flag)) {
					balDTO = new OTCBalanceCheckDTO();
					trsact_name = trsact_name + " " + "VAT";
					balDTO.setCredit_item_code(trsact_code);
					balDTO.setCredit_item(trsact_name);
					balDTO.setCredit_loc(new Double(vat_loc_amt));
					balDTO.setCredit_won(new Double(vat_won_amt));
					credit.add(balDTO);
				}

			}
			crs.add(credit);

			// credit 총합을 구함 ============================================
			ps2 = conn.prepareStatement(crTot);
			int k1 = 1;

			ps2.setLong(k1++, sa_no);
			ps2.setString(k1++, chtInOutCode);

			rs2 = ps2.executeQuery();
			balDTO = null;
			while (rs2.next()) {
				balDTO = new OTCBalanceCheckDTO();
				loc_amt = Formatter.nullDouble(new Double(rs2.getDouble("usd_amt")));
				won_amt = Formatter.nullDouble(new Double(rs2.getDouble("krw_amt")));
				trsact_name = "";
				balDTO.setCredit_item(trsact_name);
				balDTO.setCredit_loc(new Double(loc_amt));
				balDTO.setCredit_won(new Double(won_amt));
				cr_tot_loc_amt = cr_tot_loc_amt + loc_amt;
log.debug("---대변 total값(crTot)  dr_tot_loc_amt : " + cr_tot_loc_amt );
log.debug("---대변 total값(crTot)  loc_amt : " + loc_amt );


				cr_tot_won_amt = cr_tot_won_amt + won_amt;
			}
			crs.add(balDTO);

			// credit balance 총합을 구함
			// ============================================
			ps3 = conn.prepareStatement(crBal);
			int j1 = 1;

			ps3.setLong(j1++, sa_no);
			ps3.setString(j1++, chtInOutCode);

			rs3 = ps3.executeQuery();
			balDTO = null;
			while (rs3.next()) {

				balDTO = new OTCBalanceCheckDTO();
				loc_amt = Formatter.nullDouble(new Double(rs3.getDouble("usd_amt")));
				won_amt = Formatter.nullDouble(new Double(rs3.getDouble("krw_amt")));
				trsact_name = "";
				balDTO.setCredit_item(trsact_name);
				balDTO.setCredit_loc(new Double(loc_amt));
				balDTO.setCredit_won(new Double(won_amt));

				if (loc_amt > 0) {
					bhDTO.setApAr("AP");
				}

				cr_tot_loc_amt = cr_tot_loc_amt + loc_amt;
log.debug("---대변 BALANCE값(crBal)  cr_tot_loc_amt : " + cr_tot_loc_amt );
log.debug("---대변 BALANCE값(crBal)   loc_amt : " + loc_amt );

				cr_tot_won_amt = cr_tot_won_amt + won_amt;

				bhDTO.setDue_date(rs3.getTimestamp("due_date"));
				bhDTO.setPymt_term(rs3.getString("pymt_term"));
				bhDTO.setTerms_date(rs3.getTimestamp("terms_date"));
				bhDTO.setPymt_hold_flag(rs3.getString("pymt_hold_flag"));
				bhDTO.setPymt_meth(rs3.getString("pymt_meth"));
				bhDTO.setPymt_term(rs3.getString("pymt_term"));
				bhDTO.setBank_acc_id(new Long(rs3.getLong("bank_acc_id")));
				bhDTO.setBank_acc_desc(rs3.getString("bank_acc_desc"));
				//140314 GYJ
				bhDTO.setCourt_admit_no(rs3.getString("court_admit_no"));
				bhDTO.setCourt_flag(rs3.getString("court_flag"));

			}
			crs.add(balDTO);
			balDTO = new OTCBalanceCheckDTO();
			balDTO.setCr_tot_loc_amt(new Double(cr_tot_loc_amt));
			balDTO.setCr_tot_won_amt(new Double(cr_tot_won_amt));
			crs.add(balDTO);

			// 왼쪽 차변
			result.add(drs);
			// 오른쪽 대 변
			result.add(crs);

			// 윗쪽
			result.add(bhDTO);



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (rs2 != null)
				rs2.close();
			if (ps2 != null)
				ps2.close();

			if (rs3 != null)
				rs3.close();
			if (ps3 != null)
				ps3.close();
			if (rs4 != null)
				rs4.close();
			if (ps4 != null)
				ps4.close();
			if (rs5 != null)
				rs5.close();
			if (ps5 != null)
				ps5.close();
			if (rs6 != null)
				rs6.close();
			if (ps6 != null)
				ps6.close();
			if (rs7 != null)
				rs7.close();
			if (ps7 != null)
				ps7.close();
			if (rs8 != null)
				rs8.close();
			if (ps8 != null)
				ps8.close();
			if (rs9 != null)
				rs9.close();
			if (ps9 != null)
				ps9.close();
			if (rs10 != null)
				rs10.close();
			if (ps10 != null)
				ps10.close();
			if (rs11 != null)
				rs11.close();
			if (ps11 != null)
				ps11.close();

		}
		return result;

	}

	/**
	 * <p>
	 * 설명: 입력조건에 해당하는 sa 정보의 Balance 정보를 계산한다. chtInOutCOde : O, T, R
	 */
	public OTCBalanceCheckDTO saBalanceCheckCalculation(Long saNo, String chtInOutCode, UserBean userBean, Connection conn) throws Exception, STXException {

		OTCBalanceCheckDTO result = new OTCBalanceCheckDTO();

		PreparedStatement ps3 = null;
		ResultSet rs3 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;


		try {

			String crTot = "	select sum(nvl(b.usd_sa_amt,0)) + sum(nvl(b.usd_vat_sa_amt,0)) as usd_amt, sum(nvl(b.krw_sa_amt,0))+ sum(nvl(b.krw_vat_sa_amt,0)) as krw_amt  ";
			crTot = crTot + "	from otc_sa_head a, otc_sa_detail b, ccd_trsact_type_m c  ";
			crTot = crTot + "			where a.sa_no = b.sa_no  ";
			crTot = crTot + "			and a.sa_no = ?     ";
			crTot = crTot + "			and b.trsact_code = c.trsact_code  ";
			crTot = crTot + "			and c.o_sa_rpt_debit_credit = '2'   ";
			crTot = crTot + "			and c.som_system_type ='SOMO'        ";
			crTot = crTot + "			and a.cht_in_out_code  = c.own_vsl_category  ";
			crTot = crTot + "			and b.trsact_code not in ( 'L001' , 'L002')   ";

			String drTot = "			 	select  ";
			drTot = drTot + "			 sum(nvl(b.usd_sa_amt,0)) + sum(nvl(b.usd_vat_sa_amt,0)) as usd_amt, sum(nvl(b.krw_sa_amt,0))+ sum(nvl(b.krw_vat_sa_amt,0)) as krw_amt  ";
			drTot = drTot + "			from otc_sa_head a, otc_sa_detail b, ccd_trsact_type_m c  ";
			drTot = drTot + "			where a.sa_no = b.sa_no  ";
			drTot = drTot + "			and a.sa_no =?   ";
			drTot = drTot + "			and b.trsact_code = c.trsact_code  ";
			drTot = drTot + "			and c.o_sa_rpt_debit_credit = '1'  ";
			drTot = drTot + "			and c.som_system_type ='SOMO'  ";
			drTot = drTot + "			and a.cht_in_out_code  = c.own_vsl_category  ";
			drTot = drTot + "			and b.trsact_code not in ( 'L001' , 'L002')  ";

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
			ps3 = conn.prepareStatement(drTot);
			int j1 = 1;

			ps3.setLong(j1++, saNo.longValue());
			rs3 = ps3.executeQuery();

			while (rs3.next()) {
				dr_loc_amt = Formatter.nullDouble(new Double(rs3.getDouble("usd_amt")));
				dr_won_amt = Formatter.nullDouble(new Double(rs3.getDouble("krw_amt")));

			}

			// credit 총합을 구함 ============================================
			ps2 = conn.prepareStatement(crTot);
			int k1 = 1;

			ps2.setLong(k1++, saNo.longValue());
			rs2 = ps2.executeQuery();

			while (rs2.next()) {

				cr_loc_amt = Formatter.nullDouble(new Double(rs2.getDouble("usd_amt")));
				cr_won_amt = Formatter.nullDouble(new Double(rs2.getDouble("krw_amt")));

			}

			loc_amt = dr_loc_amt - cr_loc_amt;
			won_amt = dr_won_amt - cr_won_amt;

			if (loc_amt >= 0) {

				result.setBal_item_code("L001");
				result.setBal_loc(new Double(loc_amt));
				result.setBal_won(new Double(won_amt));

			} else {

				result.setBal_item_code("L002");
				loc_amt = loc_amt * -1;
				won_amt = won_amt * -1;
				result.setBal_loc(new Double(loc_amt));
				result.setBal_won(new Double(won_amt));
			}



		} catch (Exception e) {

			throw new STXException(e);

		} finally {
			if (rs2 != null)
				rs2.close();
			if (ps2 != null)
				ps2.close();

			if (rs3 != null)
				rs3.close();
			if (ps3 != null)
				ps3.close();

		}
		return result;

	}


	public double saDiffKrwAmtCalculation(Long saNo, Connection conn) throws STXException {

		double result = 0.0;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer();
			sb.append("\n   select sum(decode(c.o_sa_rpt_debit_credit, '1', nvl(b.krw_sa_amt,0)+ nvl(b.krw_vat_sa_amt,0), 0)) -			   ");
			sb.append("\n          sum(decode(c.o_sa_rpt_debit_credit, '2', nvl(b.krw_sa_amt,0)+ nvl(b.krw_vat_sa_amt,0), 0)) as diff_krw_amt      ");
			sb.append("\n   from otc_sa_head a, otc_sa_detail b, ccd_trsact_type_m c  							       ");
			sb.append("\n   where a.sa_no = b.sa_no  											       ");
			sb.append("\n   and a.sa_no = " + saNo.longValue() + " ");
			sb.append("\n   and b.trsact_code = c.trsact_code  										       ");
			sb.append("\n   and c.som_system_type ='SOMO'        										       ");
			sb.append("\n   and a.cht_in_out_code  = c.own_vsl_category  									       ");

			ps = conn.prepareStatement(sb.toString());

			log.debug("RYU saDiffKrwAmtCalculation="+sb.toString());

			rs = ps.executeQuery();

			while (rs.next()) {
				result = rs.getDouble("diff_krw_amt");
			}

			log.debug("saDiffKrwAmtCalculation="+result);

		} catch (Exception e) {

			throw new STXException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e1) {

				throw new STXException(e1);
			}
		}
		return result;
	}

	/**
	 * <p>
	 * 설명: sa Balance Check Due Date Modify한다.
	 *
	 */
	public String saBalanceDueDateModify(Long saNo, OTCBalanceHeadDTO infos, UserBean userBean, Connection conn) throws Exception, STXException {
		String result = "";
		PreparedStatement ps = null;
		try {


			if (saNo != null) {

				StringBuffer sb = new StringBuffer();
				// Query 가져오기

				sb.append("		UPDATE OTC_SA_DETAIL SET  ");
				sb.append("			     DUE_DATE = ? ,          ");
				sb.append("		       TERMS_DATE =  ? ,     ");
				sb.append("		       PYMT_HOLD_FLAG = ?,   ");
				sb.append("		       BANK_ACC_ID = ?,   ");
				sb.append("		       BANK_ACC_DESC = ?,   ");
				sb.append("		   	   SYS_UPD_DATE = SYSDATE , ");
				sb.append("			   SYS_UPD_USER_ID = ?  ");
				sb.append("		 WHERE SA_NO = ?  ");
				sb.append("		 AND TRSACT_CODE IN  ('L001' , 'L002' )");

				ps = conn.prepareStatement(sb.toString());

				int i = 1;
				ps.setTimestamp(i++, infos.getDue_date());
				ps.setTimestamp(i++, infos.getTerms_date());
				ps.setString(i++, Formatter.nullTrim(infos.getPymt_hold_flag()));
				ps.setLong(i++, Formatter.nullLong(infos.getBank_acc_id()));
				ps.setString(i++, Formatter.nullTrim(infos.getBank_acc_desc()));
				// ps.setString(i++, Formatter.nullTrim(infos.getPymt_term()));
				ps.setString(i++, Formatter.nullTrim(userBean.getUser_id()));
				ps.setLong(i++, saNo.longValue());

				ps.executeUpdate();

				result = "SUC-0600";

			}

		} catch (Exception e) {
			throw new STXException(e);
		}finally{
			if(ps != null) ps.close();
		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa open type 을 리턴하는 메소드이다.
	 *
	 * @exception STXException :
	 */
	public String saOpenType(String gl_accd) throws STXException {

		String result = "";


		try {

			if ("210802".equals(Formatter.nullTrim(gl_accd)) || "210803".equals(Formatter.nullTrim(gl_accd)) || "210809".equals(Formatter.nullTrim(gl_accd))) {
				result = "OE";
			} else if ("210805".equals(Formatter.nullTrim(gl_accd))) {
				result = "RB";
			} else if ("210402".equals(Formatter.nullTrim(gl_accd)) || "210405".equals(Formatter.nullTrim(gl_accd)) || "210403".equals(Formatter.nullTrim(gl_accd)) || "210701".equals(Formatter.nullTrim(gl_accd)) || "210499".equals(Formatter.nullTrim(gl_accd))) {		//111013 GYJ (210499)기타영업미지급금 추가
				result = "AP";
			} else if ("110902".equals(Formatter.nullTrim(gl_accd)) || "110903".equals(Formatter.nullTrim(gl_accd)) || "110907".equals(Formatter.nullTrim(gl_accd))|| "110912".equals(Formatter.nullTrim(gl_accd)) || "110913".equals(Formatter.nullTrim(gl_accd))) {
				result = "AC";
			} else if ("110502".equals(Formatter.nullTrim(gl_accd)) || "110503".equals(Formatter.nullTrim(gl_accd)) || "110599".equals(Formatter.nullTrim(gl_accd))) {	//111013 GYJ (110599)영업미수-기타잔액 추가.
				result = "AR";
			}


		} catch (Exception e) {

			throw new STXException(e);
		}
		return result;
	}

	/**
	 * <p>
	 * 설명: sa Withholding Tax의 hirage값을 구한다. Register정보를 만든다.
	 *
	 */
	public OTCSaOnHireDTO saDetailWithholdHireCal(Long saNo, Double usd_amt, Double won_amt, Connection conn) throws Exception, STXException {

		OTCSaOnHireDTO result = new OTCSaOnHireDTO();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			if (saNo != null) {

				StringBuffer sb = new StringBuffer();
				// Query 가져오기

				//sb.append("	SELECT SA_NO ,MIN(FROM_DATE) as from_date, MAX(TO_DATE) as to_date, SUM(NVL(SA_RATE_DUR,0)) as rate_dur, SUM(NVL(SA_RATE,0)) as rate FROM OTC_SA_DETAIL WHERE SA_NO = ? AND TRSACT_CODE ='A001' GROUP BY SA_NO ");
				//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
				sb.append("	SELECT SA_NO ,MIN(FROM_DATE) as from_date, MAX(TO_DATE) as to_date, SUM(NVL(SA_RATE_DUR,0)) as rate_dur, SUM(NVL(SA_RATE,0)) as rate FROM OTC_SA_DETAIL WHERE SA_NO = ? AND TRSACT_CODE IN ('A001', 'A006') GROUP BY SA_NO ");

				ps = conn.prepareStatement(sb.toString());

				int i = 1;
				ps.setLong(i++, saNo.longValue());

				rs = ps.executeQuery();

				while (rs.next()) {

					result.setDay_hire(new Double(rs.getDouble("rate")));
					result.setDur(new Double(rs.getDouble("rate_dur")));
					result.setFrom_date(rs.getTimestamp("from_date"));
					result.setTo_date(rs.getTimestamp("to_date"));

				}
				result.setSa_no(new Double(saNo.doubleValue()));
				result.setAmount_usd(usd_amt);
				result.setAmount_krw(won_amt);

			}


		} catch (Exception e) {

			throw new STXException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e1) {

				throw new STXException(e1);
			}
		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa info 내역을 삭제하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA detail 테이블에 삭제할 시 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saHeadDelete 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public String saNoAllDelete(Long saNo, Connection conn) throws STXException {

		String result = "";


		try {
			DbWrap dbWrap = new DbWrap();

			StringBuffer sb = new StringBuffer();
			// Query 가져오기
			sb.append(" select sa_no from otc_sa_detail ");

			sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");

			dbWrap.setObject(conn, OTCSaDetailVO.class, sb.toString(), 4);

			result = "SUC-0600";


		} catch (Exception e) {

			throw new STXException(e);
		}
		return result;
	}


	public String saCbDetailDelete(Long saNo, Connection conn) throws STXException {

		String result = "";


		try {
			DbWrap dbWrap = new DbWrap();

			StringBuffer sb = new StringBuffer();
			// Query 가져오기
			sb.append(" select sa_no from otc_sa_cb_detail ");

			sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");

			dbWrap.setObject(conn, OTCSaCbDetailVO.class, sb.toString(), 4);

			result = "SUC-0600";


		} catch (Exception e) {

			throw new STXException(e);
		}
		return result;
	}


	/**
	 * <p>
	 * 설명:sa detail 내역을 삭제하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호 ,trsact code
	 * @return msgCode String: SA HEAD 테이블에 삭제할 시 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                Exception을 처리한다
	 */
	public String saTrsactCodeDelete(Long saNo, String trsactCode, Connection conn) throws STXException {

		String result = "";


		try {
			DbWrap dbWrap = new DbWrap();

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append("	SELECT COMCODE_INFO_FUNC('TAX_CODE', V.TAX_CODE_FLAG) TAX_CODE_NAME, V.*    ");
			sb.append("	FROM OTC_SA_DETAIL V      ");
			sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND TRSACT_CODE = '" + trsactCode + "' ");

			dbWrap.setObject(conn, OTCSaDetailVO.class, sb.toString(), 4);

			result = "SUC-0600";

		} catch (Exception e) {

			throw new STXException(e);
		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa bunker init내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public OTCSaBunkerDTO saBunkerInitSelect(String vslCode, Long voyNo, String chtInCode, Connection conn) throws STXException, Exception {

		OTCSaBunkerDTO result = null;


		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append("	SELECT A.*, B.FO_PRICE, B.DO_PRICE   ");
			sb.append("			FROM SCT_TC_INFO_HEAD A, OTC_CP_ITEM_HEAD B   ");
			sb.append("			WHERE A.CNTR_NO = B.CNTR_NO         ");
			sb.append("			AND B.VSL_CODE = ?                  ");
			sb.append("			AND B.VOY_NO = ?                    ");
			sb.append("			AND B.CHT_IN_OUT_CODE = ?           ");



			ps = conn.prepareStatement(sb.toString());
			int i = 1;
			ps.setString(i++, vslCode);
			ps.setLong(i++, voyNo.longValue());
			ps.setString(i++, chtInCode);

			rs = ps.executeQuery();

			result = new OTCSaBunkerDTO();
			while (rs.next()) {

				result.setBod_fo_qty(new Double(0));
				result.setBod_fo_price(new Double(rs.getDouble("FO_PRICE")));
				result.setBod_do_qty(new Double(0));
				result.setBod_do_price(new Double(rs.getDouble("DO_PRICE")));
				result.setBor_fo_qty(new Double(0));
				result.setBor_fo_price(new Double(rs.getDouble("FO_PRICE")));
				result.setBor_do_qty(new Double(0));
				result.setBor_do_price(new Double(rs.getDouble("DO_PRICE")));

			}



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public Collection saOwnerACInitSearch(String vslCode, Long voyNo, String chtinCd, Double dayHires, Timestamp fromHire, Timestamp toHire, Connection conn) throws STXException, Exception {

		Collection result = null;


		PreparedStatement ps = null;
		ResultSet rs = null;

		PreparedStatement ps1 = null;
		ResultSet rs1 = null;

		PreparedStatement ps2 = null;
		ResultSet rs2 = null;

		String brok11 = "";
		String brok12 = "";
		boolean broker_check = false;

		try {

			// **************************** Brokerage 가져오기 시작
			// **************************** //
			StringBuffer sb = new StringBuffer();
			if (fromHire == null) {

				sb.append(" SELECT               ");
				sb.append("  				A.CP_ITEM_NO,  ");
				sb.append("  				A.BLST_BONUS,   ");
				sb.append("  				A.CVE,          ");
				sb.append("  				A.VOY_NO ,      ");
				sb.append("  				A.APLY_TIME_FLAG ,  ");
				sb.append("  				A.CNTR_NO  ,        ");
				sb.append("  				A.LAY_CAN_FROM_DATE  , ");
				sb.append("  				A.CNTR_ACC_CODE ,   ");
				sb.append("  				A.LAY_CAN_TO_DATE,  ");
				sb.append("  				A.BANK_NAT_CODE,    ");
				sb.append("  				A.OP_TEAM_CODE ,   ");
				sb.append("  				A.WTH_TAX_FLAG ,   ");
				sb.append("  				A.REDLY_NOTICE1 ,   ");
				sb.append("  				A.BROK_COMM_RATE  ,  ");
				sb.append("  				A.REDLY_NOTICE2,    ");
				sb.append("  				A.BROK_COMM_RATE2 , ");
				sb.append("  				A.REDLY_NOTICE3 ,   ");
				sb.append("  				A.FO_PRICE ,         ");
				sb.append("  				A.REDLY_NOTICE4,    ");
				sb.append("  				A.ILOHC   ,         ");
				sb.append("  				A.REDLY_NOTICE5  ,  ");
				sb.append("  				A.CHT_IN_OUT_CODE,  ");
				sb.append("  				A.REDLY_NOTICE6,    ");
				sb.append("  				A.NAT_CODE,         ");
				sb.append("  				A.REDLY_NOTICE7,    ");
				sb.append("  				A.BROK_ACC_CODE ,   ");
				sb.append("  				A.REDLY_NOTICE8 ,   ");
				sb.append("  				A.REDLY_NOTICE9,     ");
				sb.append("  				A.VSL_CODE,          ");
				sb.append("  				A.REDLY_NOTICE10,    ");
				sb.append("  				A.DO_PRICE ,         ");
				sb.append("  				A.CNTR_TEAM_CODE ,   ");
				sb.append("  				A.BROK_ACC_CODE2,    ");
				sb.append("  				A.SYS_CRE_DATE,     ");
				sb.append("  				A.SYS_CRE_USER_ID , ");
				sb.append("  				A.SYS_UPD_DATE,     ");
				sb.append("				    A.SYS_UPD_USER_ID ,    ");
				sb.append("  				A.BANK_NAME, ");
				sb.append("				NAT_ENG_NAME_FUNC(A.BANK_NAT_CODE) AS BANK_NAT_NAME,		");
				sb.append("				NAT_ENG_NAME_FUNC(A.NAT_CODE) AS NAT_NAME,");
				sb.append("				TEAM_INFO_FUNC(A.OP_TEAM_CODE) AS OP_TEAM_NAME, ");
				sb.append("				TEAM_INFO_FUNC(A.CNTR_TEAM_CODE) AS CNTR_TEAM_NAME, ");
				sb.append("       ACC_NAME_FUNC(A.CNTR_ACC_CODE) AS CNTR_ACC_NAME,");
				sb.append("				ACC_NAME_FUNC(A.BROK_ACC_CODE) AS BROK_ACC_NAME, ");
				sb.append("				ACC_NAME_FUNC(A.BROK_ACC_CODE2) AS BROK_ACC_NAME2,");
				sb.append("				VSL_NAME_FUNC(A.VSL_CODE) AS VSL_NAME, ");
				sb.append("				Cntr_Name_Func(A.CNTR_NO) AS CNTR_NAME, ");
				sb.append("				ACC_NATION_FUNC(A.CNTR_ACC_CODE) AS ACC_NAT_CODE, ");
				sb.append("				ACC_NATION_FUNC(A.BROK_ACC_CODE) AS BROK_NAT, ");
				sb.append("				ACC_NATION_FUNC(A.BROK_ACC_CODE2) AS BROK_NAT2, ");
				sb.append("			    NAT_ENG_NAME_FUNC(ACC_NATION_FUNC(A.CNTR_ACC_CODE)) AS ACC_NAT_NAME ");
				sb.append("			  FROM  OTC_CP_ITEM_HEAD A ");
				sb.append("			  WHERE  ");
				sb.append("           A.VSL_CODE = '" + vslCode + "' ");
				sb.append("           AND A.VOY_NO = " + voyNo.longValue() + " ");
				sb.append("           AND A.CHT_IN_OUT_CODE = '" + chtinCd + "' ");
			} else {

				sb
						.append("	SELECT A.FO_PRICE, A.DO_PRICE,A.BROK_ACC_CODE, A.BROK_COMM_RATE, ACC_NAME_FUNC(A.BROK_ACC_CODE) AS BROK_ACC_NAME, A.BROK_ACC_CODE2, A.BROK_COMM_RATE2, ACC_NAME_FUNC(A.BROK_ACC_CODE2) AS BROK_ACC_NAME2,B.CP_DATE, B.FROM_DATE, B.TO_DATE, B.DAY_HIRE, B.HIRE_DUR, B.ADDR_COMM_RATE, ");
				sb.append("				ACC_NATION_FUNC(A.BROK_ACC_CODE) AS BROK_NAT, ");
				sb.append("				ACC_NATION_FUNC(A.BROK_ACC_CODE2) AS BROK_NAT2 ");
				sb.append("	FROM OTC_CP_ITEM_HEAD A, OTC_CP_ITEM_DETAIL B ");
				sb.append("	WHERE A.CP_ITEM_NO = B.CP_ITEM_NO ");
				sb.append("		AND A.VSL_CODE = '" + vslCode + "' ");
				sb.append("		AND A.VOY_NO = " + voyNo.longValue() + "   ");
				sb.append("		AND A.CHT_IN_OUT_CODE = '" + chtinCd + "' ");
				sb.append("			  AND B.CP_ITEM_SEQ = (SELECT MIN(CP_ITEM_SEQ) ");
				sb.append("			  					   FROM OTC_CP_ITEM_DETAIL        ");
				sb.append("			  					   WHERE CP_ITEM_NO = A.CP_ITEM_NO) ");
				// sb.append(" AND B.CP_ITEM_NO = (SELECT CP_ITEM_NO FROM
				// OTC_CP_ITEM_HEAD WHERE VSL_CODE = ? AND VOY_NO = ? AND
				// CHT_IN_OUT_CODE = ? ) ");
				// sb.append(" AND B.FROM_DATE <= TO_DATE(?,'yyyymmddhh24miss')
				// AND TO_DATE(?,'yyyymmddhh24miss') <= B.TO_DATE ");
			}

			ps = conn.prepareStatement(sb.toString());

			// if (fromHire != null) {
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
			// }
			rs = ps.executeQuery();



			OTCSaBrokerageDTO brokDTO = new OTCSaBrokerageDTO();
			result = new ArrayList();
			double add_comm = 0;
			double fo_prc = 0;
			double do_prc = 0;
			while (rs.next()) {


				if (!"T".equals(chtinCd) && "KR".equals(Formatter.nullTrim(rs.getString("BROK_NAT")))) {

				} else {
					broker_check = true;
					brok11 = rs.getString("BROK_ACC_CODE");
					brokDTO.setBroker(rs.getString("BROK_ACC_CODE"));
					brokDTO.setBroker_name(Formatter.nullTrim(rs.getString("BROK_ACC_NAME")));
					brokDTO.setBrokerage_krw(new Double(0));
					brokDTO.setBrokerage_usd(new Double(0));
					brokDTO.setComm(new Double(rs.getDouble("BROK_COMM_RATE")));
					brokDTO.setRemark("");
					brokDTO.setBrok_reserve_flag("N");
				}
				if (!"T".equals(chtinCd) && "KR".equals(Formatter.nullTrim(rs.getString("BROK_NAT2")))) {

				} else {
					if (broker_check) {
						brok12 = rs.getString("BROK_ACC_CODE2");
						brokDTO.setBroker2(rs.getString("BROK_ACC_CODE2"));
						brokDTO.setBroker_name2(Formatter.nullTrim(rs.getString("BROK_ACC_NAME2")));
						brokDTO.setBrokerage_krw2(new Double(0));
						brokDTO.setBrokerage_usd2(new Double(0));
						brokDTO.setComm2(new Double(rs.getDouble("BROK_COMM_RATE2")));
						brokDTO.setRemark2("");
						brokDTO.setBrok_reserve_flag2("N");
					} else {
						brok11 = rs.getString("BROK_ACC_CODE2");
						brokDTO.setBroker(rs.getString("BROK_ACC_CODE2"));
						brokDTO.setBroker_name(Formatter.nullTrim(rs.getString("BROK_ACC_NAME2")));
						brokDTO.setBrokerage_krw(new Double(0));
						brokDTO.setBrokerage_usd(new Double(0));
						brokDTO.setComm(new Double(rs.getDouble("BROK_COMM_RATE2")));
						brokDTO.setRemark("");
						brokDTO.setBrok_reserve_flag("N");
					}
				}


				fo_prc = rs.getDouble("FO_PRICE");
				do_prc = rs.getDouble("DO_PRICE");
				if (fromHire != null) {
					dayHires = new Double(rs.getDouble("DAY_HIRE"));
					add_comm = rs.getDouble("ADDR_COMM_RATE");
				}
			}

			brokDTO.setHire(new Double(0));
			brokDTO.setHire2(new Double(0));
			result.add(brokDTO);
			// **************************** Brokerage 가져오기 종료
			// **************************** //

			// **************************** Speed Claim 가져오기 시작
			// **************************** //
			Collection speeds = new ArrayList();

			OTCSaSpeedClaimDTO speedDTO = new OTCSaSpeedClaimDTO();
			speedDTO.setAdd_comm(new Double(add_comm));
			speedDTO.setFo_price(new Double(fo_prc));

			speedDTO.setDo_price(new Double(do_prc));


			speedDTO.setDay_hire(dayHires);
			speedDTO.setSpeed_claim_flag("R"); // Reserved
			if ("R".equals(Formatter.nullTrim(speedDTO.getSpeed_claim_flag()))) {
				speedDTO.setFactor(new Double(100 - Formatter.nullDouble(speedDTO.getAdd_comm())));
			} else {
				speedDTO.setFactor(new Double(100));
			}
			speedDTO.setOrg_factor(new Double(100));
			speedDTO.setRsv_factor(new Double(100 - Formatter.nullDouble(speedDTO.getAdd_comm())));
			speeds.add(speedDTO);
			result.add(speeds);
			// **************************** Speed Claim 가져오기 종료
			// **************************** //
			Collection acs = new ArrayList();

			result.add(acs);
			// **************************** Owner's A/C 가져오기 종료
			// **************************** //

			// **************************** bank info 가져오기 시작
			// **************************** //
			// DbWrap dbWrap = new DbWrap();
			StringBuffer sb8 = new StringBuffer();
			sb8.append(" SELECT  ");
			sb8.append(" bank.BANK_ACCOUNT_ID as bank_acc_id,");
			sb8.append(" bank.BANK_NAME|| '-' || bank.BANK_BRANCH_NAME ||'   ' || bank.BANK_ACCOUNT_NUM as bank_acc_desc ");
			sb8.append(" FROM EAR_VENDOR_BANK_ACCOUNT_V bank ");
			sb8.append(" where bank.customer_unique_id='" + brok11 + "'");


			ps1 = conn.prepareStatement(sb8.toString());
			rs1 = ps1.executeQuery();

			EARVendorBankAccountVDTO bankDTO = new EARVendorBankAccountVDTO();
			// 빈공백을 처음에 넣는다. 빈공백도 선택가능하다.
			bankDTO.setBank_acc_id(new Long(0));
			bankDTO.setBank_acc_desc("");

			Collection bankInfo1 = new ArrayList();
			bankInfo1.add(bankDTO);

			while (rs1.next()) {

				bankDTO = new EARVendorBankAccountVDTO();

				bankDTO.setBank_acc_id(new Long(rs1.getLong("BANK_ACC_ID")));
				bankDTO.setBank_acc_desc(Formatter.nullTrim(rs1.getString("BANK_ACC_DESC")));

				bankInfo1.add(bankDTO);
			}

			result.add(bankInfo1);

			StringBuffer sb81 = new StringBuffer();
			sb81.append(" SELECT  ");
			sb81.append(" bank.BANK_ACCOUNT_ID as bank_acc_id,");
			sb81.append(" bank.BANK_NAME|| '-' || bank.BANK_BRANCH_NAME ||'   ' || bank.BANK_ACCOUNT_NUM as bank_acc_desc ");
			sb81.append(" FROM EAR_VENDOR_BANK_ACCOUNT_V bank ");
			sb81.append(" where bank.customer_unique_id='" + brok12 + "'");

			ps2 = conn.prepareStatement(sb81.toString());
			//rs2 = ps1.executeQuery();
			rs2 = ps2.executeQuery();		//1.8로 올리고 "결과 집합을 종료했음: next" 라는 에러가 나서 수정 220518

			bankDTO = new EARVendorBankAccountVDTO();
			// 빈공백을 처음에 넣는다. 빈공백도 선택가능하다.
			bankDTO.setBank_acc_id(new Long(0));
			bankDTO.setBank_acc_desc("");

			Collection bankInfo2 = new ArrayList();
			bankInfo1.add(bankDTO);

			//while (rs1.next()) {
			while (rs2.next()) {			//1.8로 올리고 "결과 집합을 종료했음: next" 라는 에러가 나서 수정 220518

				bankDTO = new EARVendorBankAccountVDTO();

				/*bankDTO.setBank_acc_id(new Long(rs.getLong("BANK_ACC_ID")));
				bankDTO.setBank_acc_desc(Formatter.nullTrim(rs.getString("BANK_ACC_DESC")));
				*/
				//1.8로 올리고 "결과 집합을 종료했음: next" 라는 에러가 나서 수정 220518
				bankDTO.setBank_acc_id(new Long(rs2.getLong("BANK_ACC_ID")));
				bankDTO.setBank_acc_desc(Formatter.nullTrim(rs2.getString("BANK_ACC_DESC")));

				bankInfo2.add(bankDTO);
			}


			result.add(bankInfo2);



		} catch (Exception e) {

			throw new STXException(e);
		} finally {

			if (rs != null)
				rs.close();
			if (rs1 != null)
				rs1.close();
			if (rs2 != null)
				rs2.close();
			if (ps != null)
				ps.close();
			if (ps1 != null)
				ps1.close();
			if (ps2 != null)
				ps2.close();
		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saOffHireSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public Collection saOffHireInitSelect(String vslCode, Long voyNo, String chtInCode, Timestamp fromHire, Timestamp toHire, Connection conn) throws STXException, Exception {

		Collection result = new ArrayList();


		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			// **************************** NegoAmount/Compensation 가져오기 시작
			// **************************** //

			OTCSaOffHireNegoDTO negoDTO = new OTCSaOffHireNegoDTO();

			result.add(negoDTO);
			// **************************** NegoAmount/Compensation 가져오기 종료
			// **************************** //

			// **************************** Off Hire 가져오기 시작
			// **************************** //

			StringBuffer sb = new StringBuffer();


			sb.append(" SELECT A.*, ACC_NATION_FUNC(A.CNTR_ACC_CODE) AS ACC_NAT_CODE, B.CP_DATE, B.FROM_DATE, B.TO_DATE, B.DAY_HIRE, B.HIRE_DUR, B.ADDR_COMM_RATE ");
			sb.append("			FROM OTC_CP_ITEM_HEAD A, OTC_CP_ITEM_DETAIL B ");
			sb.append("			WHERE A.CP_ITEM_NO = B.CP_ITEM_NO ");
			sb.append("			  AND A.VSL_CODE = ? ");
			sb.append("			  AND A.VOY_NO = ?   ");
			sb.append("			  AND A.CHT_IN_OUT_CODE = ? ");
			sb.append("			  AND B.CP_ITEM_SEQ = (SELECT MIN(CP_ITEM_SEQ) ");
			sb.append("			  					   FROM OTC_CP_ITEM_DETAIL        ");
			sb.append("			  					   WHERE CP_ITEM_NO = A.CP_ITEM_NO) ");

			int i = 1;

			ps = conn.prepareStatement(sb.toString());

			ps.setString(i++, vslCode);
			ps.setLong(i++, voyNo.longValue());
			ps.setString(i++, chtInCode);

			rs = ps.executeQuery();



			OTCSaOffHireDTO offDTO = null;
			Collection offs = new ArrayList();

			while (rs.next()) {
				offDTO = new OTCSaOffHireDTO();
				if ("".equals(Formatter.nullTrim(offDTO.getStl_flag()))) {
					offDTO.setStl_flag("A");
				}
				if ("".equals(Formatter.nullTrim(offDTO.getOwn_spd_clm_flag()))) {
					offDTO.setOwn_spd_clm_flag("N");
				}  // Actual(Net)
				offDTO.setDay_hire(new Double(rs.getDouble("DAY_HIRE")));
				offDTO.setAdd_comm(new Double(rs.getDouble("ADDR_COMM_RATE")));
				offDTO.setFo_price(new Double(rs.getDouble("FO_PRICE")));
				offDTO.setDo_price(new Double(rs.getDouble("DO_PRICE")));
				offDTO.setFo_idle(new Double(rs.getDouble("FO_IDLE")));
				offDTO.setDo_idle(new Double(rs.getDouble("DO_IDLE")));
				offDTO.setFactor(new Double(100));
				offs.add(offDTO);
				offDTO = new OTCSaOffHireDTO();
				if ("".equals(Formatter.nullTrim(offDTO.getStl_flag()))) {
					offDTO.setStl_flag("A");
				}
				if ("".equals(Formatter.nullTrim(offDTO.getOwn_spd_clm_flag()))) {
					offDTO.setOwn_spd_clm_flag("N");
				}  // Actual(Net)
				offDTO.setDay_hire(new Double(rs.getDouble("DAY_HIRE")));
				offDTO.setAdd_comm(new Double(rs.getDouble("ADDR_COMM_RATE")));
				offDTO.setFo_price(new Double(rs.getDouble("FO_PRICE")));
				offDTO.setDo_price(new Double(rs.getDouble("DO_PRICE")));
				offDTO.setFo_idle(new Double(rs.getDouble("FO_IDLE")));
				offDTO.setDo_idle(new Double(rs.getDouble("DO_IDLE")));
				offDTO.setFactor(new Double(100));
				offs.add(offDTO);

			} // while
			result.add(offs);
			// **************************** Off Hire 가져오기 종료
			// **************************** //


		} catch (Exception e) {

			throw new STXException(e);
		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail 내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public Collection saDetailBySaNoSearch(Long saNo, Connection conn) throws STXException {

		Collection result = null;


		try {
			DbWrap dbWrap = new DbWrap();

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append("	select a.*,trsact_name_func('SOMO',b.cht_in_out_code,a.trsact_code) as trsact_name, b.posting_date, b.cht_in_out_code, b.op_team_code from otc_sa_detail a, otc_sa_head b where a.sa_no = b.sa_no  ");

			sb.append(" AND A.SA_NO = " + saNo.longValue() + " ");

log.debug("saDetailBySaNoSearch : "+ sb.toString());

			//result = dbWrap.getObjects(conn, OTCSaDetailDTO.class, sb.toString());
			result = dbWrap.getObjects(conn, OTCSaCbDetailDTO.class, sb.toString());	//향후 무조건 CbDetailDTO 사용할 것. 150126 GYJ

		} catch (Exception e) {

			throw new STXException(e);
		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Owner Settle역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 */
	public Collection saOwnerSettleRsvUnSubmitSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo, String stlFlag, String processFlag, Connection conn) throws STXException, Exception {

		Collection result = new ArrayList();
		Collection rev = new ArrayList();
		Collection act = new ArrayList();
		Collection brk = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;

		PreparedStatement ps3 = null;
		ResultSet rs3 = null;
		PreparedStatement ps31 = null;
		ResultSet rs31 = null;
		PreparedStatement ps32 = null;
		ResultSet rs32 = null;

		// String stlFlagCk = "";

		// String sExist = "";

		try {

			//String invoice = "	 select d.* , a.stl_cntr_acc_code,c.op_team_code, c.cntr_team_code,    ";
			String invoice = "	 select /*+ NO_MERGE(D) NO_MERGE(D.aida_mc) PUSH_PRED(D.aida_mc) NO_MERGE(D.AI_USD) PUSH_PRED(D.AI_USD) */ d.* , a.stl_cntr_acc_code,c.op_team_code, c.cntr_team_code,        ";		//19c 튜닝 220607
			invoice = invoice + "		      ACC_NAME_FUNC(a.stl_cntr_acc_code) as stl_acc_name ,     ";
			invoice = invoice + "			  a.curcy_code as currency_code,  d.gl_date,   ";
			invoice = invoice + "		  (nvl(d.entered_balance_amount,0)-  nvl(d.entered_pending_amount,0)) as entered_amt,     ";
			invoice = invoice + "		  (nvl(d.usd_balance_amount,0)- nvl(d.usd_pending_amount,0)) as usd_amt,    ";
			invoice = invoice + "		  (nvl(d.krw_balance_amount,0)- nvl(d.krw_pending_amount,0)) as won_amt,    ";
			invoice = invoice + "			  a.stl_erp_slip_no  as slip_no,     ";
			invoice = invoice + "			  a.stl_gl_acc_code as gl_acct,    ";
			invoice = invoice + "	         c.sa_no, a.stl_flag, a.stl_vsl_code, a.stl_voy_no, a.stl_erp_slip_no, a.usd_sa_amt,    ";
			invoice = invoice + "	         a.loc_sa_amt, a.krw_sa_amt, a.stl_port_code, a.remark, a.curcy_code,     ";
			invoice = invoice + "	         a.exc_date, a.exc_rate_type, a.usd_exc_rate, a.loc_exc_rate,a.usd_loc_rate,     ";
			invoice = invoice + "	         a.due_date, a.terms_date, a.pymt_term, a.pymt_hold_flag    ";
			invoice = invoice + "		 from EAR_IF_INVOICE_BALANCE_V d, otc_sa_head c, otc_sa_detail a     ";
			invoice = invoice + "		 where a.sa_no = c.sa_no    ";
			invoice = invoice + "	 AND a.STL_ERP_SLIP_NO = d.INVOICE_NUMBER       ";

			// **************************** Reserved(Owners' Exp) 가져오기 시작
			// **************************** //
			StringBuffer sb = new StringBuffer();
			sb.append(invoice);

			sb.append(" and a.sa_no  = " + saNo.longValue() + " ");

			sb.append(" and a.stl_gl_acc_code IN ('210802' ,'210803' ,'210809') ");


			log.debug(">> saOwnerSettleRsvUnSubmitSearch 쿼리문 \n : " + sb.toString() );
			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaOwnSettleDTO settleDTO = null;

			while (rs.next()) {

				settleDTO = new OTCSaOwnSettleDTO();
				settleDTO.setCheck_item("1");
				settleDTO.setOp_team_code(Formatter.nullTrim(rs.getString("op_team_code")));
				settleDTO.setCntr_team_code(Formatter.nullTrim(rs.getString("cntr_team_code")));
				settleDTO.setStl_acc_code(Formatter.nullTrim(rs.getString("stl_cntr_acc_code")));
				settleDTO.setStl_acc_name(Formatter.nullTrim(rs.getString("stl_acc_name")));
				settleDTO.setCurrency_code(Formatter.nullTrim(rs.getString("currency_code")));
				settleDTO.setEntered_amt(new Double(rs.getDouble("entered_amt")));
				settleDTO.setUsd_amt(new Double(rs.getDouble("usd_amt")));
				settleDTO.setWon_amt(new Double(rs.getDouble("won_amt")));
				settleDTO.setSlip_no(Formatter.nullTrim(rs.getString("slip_no")));
				settleDTO.setGl_acct(Formatter.nullTrim(rs.getString("gl_acct")));

				settleDTO.setSa_no(new Double(rs.getDouble("sa_no")));
				settleDTO.setStl_flag(Formatter.nullTrim(rs.getString("stl_flag")));
				// stlFlagCk = settleDTO.getStl_flag();
				settleDTO.setStl_vsl_code(Formatter.nullTrim(rs.getString("stl_vsl_code")));
				settleDTO.setStl_voy_no(new Long(rs.getLong("stl_voy_no")));
				settleDTO.setUsd_sa_amt(new Double(rs.getDouble("usd_sa_amt")));
				settleDTO.setLoc_sa_amt(new Double(rs.getDouble("loc_sa_amt")));
				settleDTO.setKrw_sa_amt(new Double(rs.getDouble("krw_sa_amt")));
				settleDTO.setStl_port_code(Formatter.nullTrim(rs.getString("stl_port_code")));
				settleDTO.setStl_erp_slip_no(Formatter.nullTrim(rs.getString("stl_erp_slip_no")));
				settleDTO.setRemark(Formatter.nullTrim(rs.getString("remark")));
				settleDTO.setCurcy_code(Formatter.nullTrim(rs.getString("curcy_code")));
				settleDTO.setExc_date(rs.getTimestamp("exc_date"));
				settleDTO.setExc_rate_type(Formatter.nullTrim(rs.getString("exc_rate_type")));
				settleDTO.setUsd_exc_rate(new Double(rs.getDouble("usd_exc_rate")));
				settleDTO.setLoc_exc_rate(new Double(rs.getDouble("loc_exc_rate")));
				settleDTO.setUsd_loc_rate(new Double(rs.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_usd(new Double(rs.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_krw(new Double(rs.getDouble("loc_exc_rate")));
				//settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs.getTimestamp("exc_date")));
				settleDTO.setExchange_rate_date_krw(rs.getTimestamp("exc_date"));  //RYU
				settleDTO.setDue_date(rs.getTimestamp("due_date"));
				settleDTO.setGl_date(rs.getTimestamp("gl_date"));
				settleDTO.setPymt_term(Formatter.nullTrim(rs.getString("pymt_term")));
				settleDTO.setTerms_date(rs.getTimestamp("terms_date"));
				settleDTO.setPymt_hold_flag(Formatter.nullTrim(rs.getString("pymt_hold_flag")));

				rev.add(settleDTO);

			}

			result.add(rev);
			// **************************** Reserved(Owners' Exp) 가져오기 종료
			// **************************** //

			// **************************** Actual Owners' A/C(Tc/In) 가져오기 시작
			// **************************** //

			String trx = " 		 SELECT   /*+ PUSH_PRED(D) */  ";
			trx = trx + "	 			A.STL_CNTR_ACC_CODE,  ";
			trx = trx + "	 			C.OP_TEAM_CODE,   ";
			trx = trx + "	 			C.CNTR_TEAM_CODE,  ";
			trx = trx + "	 			ACC_NAME_FUNC(A.STL_CNTR_ACC_CODE) AS STL_ACC_NAME ,  ";
			trx = trx + "	 			A.CURCY_CODE AS CURRENCY_CODE,  ";
			trx = trx + "	 			(NVL(D.ENTERED_BALANCE_AMOUNT,0)- NVL(D.ENTERED_PENDING_AMOUNT,0)) AS ENTERED_AMT,  ";
			trx = trx + "	 			(NVL(D.USD_BALANCE_AMOUNT,0)- NVL(D.USD_PENDING_AMOUNT,0)) AS USD_AMT,  ";
			trx = trx + "	 			(NVL(D.KRW_BALANCE_AMOUNT,0)- NVL(D.KRW_PENDING_AMOUNT,0)) AS WON_AMT, ";
			trx = trx + "	 			A.STL_ERP_SLIP_NO AS SLIP_NO,  ";
			trx = trx + "	 			A.STL_GL_ACC_CODE AS GL_ACCT,  ";
			trx = trx + "	 			C.SA_NO, A.STL_FLAG, A.STL_VSL_CODE, A.STL_VOY_NO, A.STL_ERP_SLIP_NO, A.USD_SA_AMT,  ";
			trx = trx + "	 			A.LOC_SA_AMT, A.KRW_SA_AMT, A.STL_PORT_CODE, A.REMARK, A.CURCY_CODE,  ";
			trx = trx + "	 			A.EXC_DATE, A.EXC_RATE_TYPE, A.USD_EXC_RATE, A.LOC_EXC_RATE,A.USD_LOC_RATE,  ";
			trx = trx + "	 			A.DUE_DATE, A.TERMS_DATE, A.PYMT_TERM, A.PYMT_HOLD_FLAG,  ";
			trx = trx + "	 			D.TRX_ID,  ";
			trx = trx + "	 			D.TRX_NUMBER,  ";
			trx = trx + "	 			D.GL_DATE,      ";
			trx = trx + "	 			D.SOURCE_SYSTEM,  ";
			trx = trx + "	 			D.SOURCE_TRX_NUMBER,  ";
			trx = trx + "	 			D.SOURCE_TRX_ITEM_NUMBER,  ";
			trx = trx + "	 			D.CURRENCY_CODE,   ";
			trx = trx + "	 			D.ENTERED_TRX_AMOUNT,  ";
			trx = trx + "	 			D.KRW_TRX_AMOUNT,   ";
			trx = trx + "	 			D.USD_TRX_AMOUNT,    ";
			trx = trx + "	 			D.DUE_DATE,           ";
			trx = trx + "	 			D.CUSTOMER_UNIQUE_ID,  ";
			trx = trx + "	 			D.CUSTOMER_NUMBER,      ";
			trx = trx + "	 			D.CUSTOMER_NAME,  ";
			trx = trx + "	 			D.COUNTRY,  ";
			trx = trx + "	 			D.PORT_CODE,  ";
			trx = trx + "	 			D.COMMENTS,  ";
			trx = trx + "	 			D.CREATED_USER,  ";
			trx = trx + "	 			D.SEGMENT1,  ";
			trx = trx + "	 			D.SEGMENT2, ";
			trx = trx + "	 			D.SEGMENT3, ";
			trx = trx + "	 			D.SEGMENT4,  ";
			trx = trx + "	 			D.SEGMENT5,  ";
			trx = trx + "	 			D.SEGMENT6,   ";
			trx = trx + "	 			D.SEGMENT7,   ";
			trx = trx + "	 			D.EXCHANGE_RATE_TYPE_KRW,  ";
			trx = trx + "	 			D.EXCHANGE_RATE_DATE_KRW,  ";
			trx = trx + "	 			D.EXCHANGE_RATE_KRW, ";
			trx = trx + "	 			D.EXCHANGE_RATE_TYPE_USD, ";
			trx = trx + "	 			D.EXCHANGE_RATE_DATE_USD, ";
			trx = trx + "	 			D.EXCHANGE_RATE_USD, ";
			trx = trx + "	 			D.IF_TYPE_ID  ";
			trx = trx + "	 			FROM EAR_IF_TRX_BALANCE_V D, OTC_SA_HEAD C, OTC_SA_DETAIL A  ";
			trx = trx + "	 			WHERE A.SA_NO = C.SA_NO  ";
			trx = trx + "	 			AND A.STL_ERP_SLIP_NO = D.TRX_NUMBER  ";

			StringBuffer sb2 = new StringBuffer();
			sb2.append(trx);

			sb2.append(" and a.sa_no  = " + saNo.longValue() + " ");

			sb2.append(" and a.stl_gl_acc_code IN ('110902' ,'110903' ,'110907', '110912', '110913') ");



			ps2 = conn.prepareStatement(sb2.toString());
			rs2 = ps2.executeQuery();

			while (rs2.next()) {
				settleDTO = new OTCSaOwnSettleDTO();
				settleDTO.setCheck_item("1");
				settleDTO.setOp_team_code(Formatter.nullTrim(rs2.getString("op_team_code")));
				settleDTO.setCntr_team_code(Formatter.nullTrim(rs2.getString("cntr_team_code")));
				settleDTO.setStl_acc_code(Formatter.nullTrim(rs2.getString("stl_cntr_acc_code")));
				settleDTO.setStl_acc_name(Formatter.nullTrim(rs2.getString("stl_acc_name")));
				settleDTO.setCurrency_code(Formatter.nullTrim(rs2.getString("currency_code")));
				settleDTO.setEntered_amt(new Double(rs2.getDouble("entered_amt")));
				settleDTO.setUsd_amt(new Double(rs2.getDouble("usd_amt")));
				settleDTO.setWon_amt(new Double(rs2.getDouble("won_amt")));
				settleDTO.setSlip_no(Formatter.nullTrim(rs2.getString("slip_no")));
				settleDTO.setGl_acct(Formatter.nullTrim(rs2.getString("gl_acct")));

				settleDTO.setSa_no(new Double(rs2.getDouble("sa_no")));
				settleDTO.setStl_flag(Formatter.nullTrim(rs2.getString("stl_flag")));
				// stlFlagCk = settleDTO.getStl_flag();
				settleDTO.setStl_vsl_code(Formatter.nullTrim(rs2.getString("stl_vsl_code")));
				settleDTO.setStl_voy_no(new Long(rs2.getLong("stl_voy_no")));
				settleDTO.setUsd_sa_amt(new Double(rs2.getDouble("usd_sa_amt")));
				settleDTO.setLoc_sa_amt(new Double(rs2.getDouble("loc_sa_amt")));
				settleDTO.setKrw_sa_amt(new Double(rs2.getDouble("krw_sa_amt")));
				settleDTO.setStl_port_code(Formatter.nullTrim(rs2.getString("stl_port_code")));
				settleDTO.setStl_erp_slip_no(Formatter.nullTrim(rs2.getString("stl_erp_slip_no")));
				settleDTO.setRemark(Formatter.nullTrim(rs2.getString("remark")));
				settleDTO.setCurcy_code(Formatter.nullTrim(rs2.getString("curcy_code")));
				settleDTO.setExc_date(rs2.getTimestamp("exc_date"));
				settleDTO.setExc_rate_type(Formatter.nullTrim(rs2.getString("exc_rate_type")));
				settleDTO.setUsd_exc_rate(new Double(rs2.getDouble("usd_exc_rate")));
				settleDTO.setLoc_exc_rate(new Double(rs2.getDouble("loc_exc_rate")));
				settleDTO.setUsd_loc_rate(new Double(rs2.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_usd(new Double(rs2.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_krw(new Double(rs2.getDouble("loc_exc_rate")));
				//settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs2.getTimestamp("exc_date")));
				settleDTO.setExchange_rate_date_krw(rs2.getTimestamp("exc_date"));   //RYU
				settleDTO.setDue_date(rs2.getTimestamp("due_date"));
				settleDTO.setGl_date(rs2.getTimestamp("gl_date"));
				settleDTO.setPymt_term(Formatter.nullTrim(rs2.getString("pymt_term")));
				settleDTO.setTerms_date(rs2.getTimestamp("terms_date"));
				settleDTO.setPymt_hold_flag(Formatter.nullTrim(rs2.getString("pymt_hold_flag")));

				act.add(settleDTO);

			}

			result.add(act);
			// **************************** Actual Owners' A/C(Tc/In) 가져오기 종료
			// **************************** //

			// **************************** Reserved(Brokerage) 가져오기 시작
			// **************************** //
			StringBuffer sb1 = new StringBuffer();
			sb1.append(invoice);

			sb1.append(" and a.sa_no  = " + saNo.longValue() + " ");

			sb1.append(" and a.stl_gl_acc_code = '210805'  ");



			ps1 = conn.prepareStatement(sb1.toString());
			rs1 = ps1.executeQuery();

			while (rs1.next()) {
				settleDTO = new OTCSaOwnSettleDTO();
				settleDTO.setCheck_item("1");
				settleDTO.setOp_team_code(Formatter.nullTrim(rs1.getString("op_team_code")));
				settleDTO.setCntr_team_code(Formatter.nullTrim(rs1.getString("cntr_team_code")));
				settleDTO.setStl_acc_code(Formatter.nullTrim(rs1.getString("stl_cntr_acc_code")));
				settleDTO.setStl_acc_name(Formatter.nullTrim(rs1.getString("stl_acc_name")));
				settleDTO.setCurrency_code(Formatter.nullTrim(rs1.getString("currency_code")));
				settleDTO.setEntered_amt(new Double(rs1.getDouble("entered_amt")));
				settleDTO.setUsd_amt(new Double(rs1.getDouble("usd_amt")));
				settleDTO.setWon_amt(new Double(rs1.getDouble("won_amt")));
				settleDTO.setSlip_no(Formatter.nullTrim(rs1.getString("slip_no")));
				settleDTO.setGl_acct(Formatter.nullTrim(rs1.getString("gl_acct")));

				settleDTO.setSa_no(new Double(rs1.getDouble("sa_no")));
				settleDTO.setStl_flag(Formatter.nullTrim(rs1.getString("stl_flag")));
				// stlFlagCk = settleDTO.getStl_flag();
				settleDTO.setStl_vsl_code(Formatter.nullTrim(rs1.getString("stl_vsl_code")));
				settleDTO.setStl_voy_no(new Long(rs1.getLong("stl_voy_no")));
				settleDTO.setUsd_sa_amt(new Double(rs1.getDouble("usd_sa_amt")));
				settleDTO.setLoc_sa_amt(new Double(rs1.getDouble("loc_sa_amt")));
				settleDTO.setKrw_sa_amt(new Double(rs1.getDouble("krw_sa_amt")));
				settleDTO.setStl_port_code(Formatter.nullTrim(rs1.getString("stl_port_code")));
				settleDTO.setStl_erp_slip_no(Formatter.nullTrim(rs1.getString("stl_erp_slip_no")));
				settleDTO.setRemark(Formatter.nullTrim(rs1.getString("remark")));
				settleDTO.setCurcy_code(Formatter.nullTrim(rs1.getString("curcy_code")));
				settleDTO.setExc_date(rs1.getTimestamp("exc_date"));
				settleDTO.setExc_rate_type(Formatter.nullTrim(rs1.getString("exc_rate_type")));
				settleDTO.setUsd_exc_rate(new Double(rs1.getDouble("usd_exc_rate")));
				settleDTO.setLoc_exc_rate(new Double(rs1.getDouble("loc_exc_rate")));
				settleDTO.setUsd_loc_rate(new Double(rs1.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_usd(new Double(rs1.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_krw(new Double(rs1.getDouble("loc_exc_rate")));
				//settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs1.getTimestamp("exc_date")));
				settleDTO.setExchange_rate_date_krw(rs1.getTimestamp("exc_date"));   //RYU
				settleDTO.setDue_date(rs1.getTimestamp("due_date"));
				settleDTO.setGl_date(rs1.getTimestamp("gl_date"));
				settleDTO.setPymt_term(Formatter.nullTrim(rs1.getString("pymt_term")));
				settleDTO.setTerms_date(rs1.getTimestamp("terms_date"));
				settleDTO.setPymt_hold_flag(Formatter.nullTrim(rs1.getString("pymt_hold_flag")));

				brk.add(settleDTO);

			}

			result.add(brk);
			// **************************** Reserved(Brokerage) 가져오기 종료
			// **************************** //



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();
			if (rs2 != null)
				rs2.close();
			if (ps2 != null)
				ps2.close();
			if (rs3 != null)
				rs3.close();
			if (ps3 != null)
				ps3.close();
			if (rs31 != null)
				rs31.close();
			if (ps31 != null)
				ps31.close();
			if (rs32 != null)
				rs32.close();
			if (ps32 != null)
				ps32.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Owner Settle역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 */
	public Collection saOwnerSettleApUnSubmitSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo, String stlFlag, String processFlag, Connection conn) throws STXException, Exception {

		Collection result = new ArrayList();
		Collection rev = new ArrayList();
		Collection act = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;

		PreparedStatement ps3 = null;
		ResultSet rs3 = null;
		PreparedStatement ps31 = null;
		ResultSet rs31 = null;
		// String stlFlagCk = "";


		try {

			// **************************** Account payable / Advance Received
			// 가져오기 시작 **************************** //

			StringBuffer sb = new StringBuffer();
			// sb.append(invoice);
			//sb.append(" select  ");
			sb.append(" select /*+ NO_MERGE(D) NO_MERGE(D.aida_mc) PUSH_PRED(D.aida_mc) NO_MERGE(D.AI_USD) PUSH_PRED(D.AI_USD) */  ");	//19c 튜닝 220607  (EAR_IF_INVOICE_BALANCE_V써서 원천테이블과 조인하는경우 느리면 해당 힌트 써볼것)
			sb.append(" d.invioce_id,   ");
			sb.append(" d.invoice_number, ");
			sb.append(" d.gl_date, ");
			sb.append(" d.source_system, ");
			sb.append(" d.source_trx_number, ");
			sb.append(" d.source_trx_item_number, ");
			sb.append(" d.currency_code, ");
			sb.append(" d.entered_invoice_amount, ");
			sb.append(" d.entered_balance_amount, ");
			sb.append(" d.entered_pending_amount, ");
			sb.append(" d.krw_invoice_amount, ");
			sb.append(" d.krw_balance_amount, ");
			sb.append(" d.krw_pending_amount, ");
			sb.append(" d.usd_invoice_amount, ");
			sb.append(" d.usd_balance_amount, ");
			sb.append(" d.usd_pending_amount, ");
			sb.append(" d.due_date as due_date1, ");
			sb.append(" d.customer_unique_id, ");
			sb.append(" d.vendor_number,  ");
			sb.append(" d.vendor_name, ");
			sb.append(" d.country, ");
			sb.append(" d.port_code, ");
			sb.append(" d.comments, ");
			sb.append(" d.created_user,  ");
			sb.append(" d.segment1, ");
			sb.append(" d.segment2, ");
			sb.append(" d.segment3, ");
			sb.append(" d.segment4, ");
			sb.append(" d.segment5, ");
			sb.append(" d.segment6,  ");
			sb.append(" d.segment7, ");
			sb.append(" d.exchange_rate_type_krw, ");
			sb.append(" d.exchange_rate_date_krw, ");
			sb.append(" d.exchange_rate_krw,  ");
			sb.append(" d.exchange_rate_type_usd, ");
			sb.append(" d.exchange_rate_date_usd, ");
			sb.append(" d.exchange_rate_usd, ");
			sb.append(" d.if_type_id, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" a.stl_cntr_acc_code,c.op_team_code, c.cntr_team_code, ");
			sb.append(" ACC_NAME_FUNC(a.stl_cntr_acc_code) as stl_acc_name , ");
			sb.append(" a.curcy_code as currency_code, ");
			sb.append("  (nvl(d.entered_balance_amount,0)-  nvl(d.entered_pending_amount,0)) as entered_amt, ");
			sb.append("  (nvl(d.usd_balance_amount,0)- nvl(d.usd_pending_amount,0)) as usd_amt, ");
			sb.append("  (nvl(d.krw_balance_amount,0)- nvl(d.krw_pending_amount,0)) as won_amt, ");
			sb.append("  a.stl_erp_slip_no  as slip_no, ");
			sb.append("  a.stl_gl_acc_code as gl_acct, ");
			sb.append("   c.sa_no, a.stl_flag, a.stl_vsl_code, a.stl_voy_no, a.stl_erp_slip_no, a.usd_sa_amt, ");
			sb.append("    a.loc_sa_amt, a.krw_sa_amt, a.stl_port_code, a.remark, a.curcy_code, ");
			sb.append(" a.exc_date, a.exc_rate_type, a.usd_exc_rate, a.loc_exc_rate,a.usd_loc_rate, ");
			sb.append("   a.due_date, a.terms_date, a.pymt_term, a.pymt_hold_flag  ");
			sb.append("  from EAR_IF_INVOICE_BALANCE_V d, otc_sa_head c, otc_sa_detail a  ");
			sb.append("  where a.sa_no = c.sa_no  ");
			sb.append(" AND a.STL_ERP_SLIP_NO = d.INVOICE_NUMBER  ");
			sb.append(" and a.sa_no  = " + saNo.longValue() + " ");
			sb.append(" and a.stl_gl_acc_code IN ('210403' ,'210405' ,'210402','210701','210499') ");	//111017 GYJ 210499-기타영업미지급금 추가.

			sb.append(" union all ");
			// sb.append(receipt);
			sb.append(" select ");
			sb.append(" d.receipt_id, ");
			sb.append(" d.receipt_number, ");
			sb.append(" d.gl_date, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" d.currency_code, ");
			sb.append(" d.entered_receipt_amount, ");
			sb.append(" d.entered_balance_amount, ");
			sb.append(" d.entered_pending_amount, ");
			sb.append(" d.krw_receipt_amount, ");
			sb.append(" d.krw_balance_amount, ");
			sb.append(" d.krw_pending_amount, ");
			sb.append(" d.usd_receipt_amount, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" d.customer_unique_id, ");
			sb.append(" d.customer_number, ");
			sb.append(" d.customer_name, ");
			sb.append(" d.country, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" NULL, ");
			sb.append(" d.exchange_rate_type_krw, ");
			sb.append(" d.exchange_rate_date_krw,");
			sb.append(" d.exchange_rate_krw,");
			sb.append(" d.exchange_rate_type_usd,");
			sb.append(" d.exchange_rate_date_usd,");
			sb.append(" d.exchange_rate_usd,");
			sb.append(" NULL,");
			sb.append(" d.receipt_book_number,");
			sb.append(" d.paymnet_user,");
			sb.append(" d.comments,");
			sb.append(" d.created_user,");
			sb.append(" d.payment_method,");
			sb.append(" d.vessel_code,");
			sb.append(" d.voyage_no,");
			sb.append(" a.stl_cntr_acc_code,c.op_team_code, c.cntr_team_code,");
			sb.append("  ACC_NAME_FUNC(a.stl_cntr_acc_code) as stl_acc_name ,");
			sb.append("  a.curcy_code as currency_code, ");
			sb.append(" (nvl(d.entered_balance_amount,0)-  nvl(d.entered_pending_amount,0)) as entered_amt, ");
			sb.append(" (nvl(d.usd_balance_amount,0)- nvl(d.usd_pending_amount,0)) as usd_amt, ");
			sb.append(" (nvl(d.krw_balance_amount,0)- nvl(d.krw_pending_amount,0)) as won_amt, ");
			sb.append(" a.stl_erp_slip_no  as slip_no, ");
			sb.append(" a.stl_gl_acc_code as gl_acct, ");
			sb.append(" c.sa_no, a.stl_flag, a.stl_vsl_code, a.stl_voy_no, a.stl_erp_slip_no, a.usd_sa_amt, ");
			sb.append(" a.loc_sa_amt, a.krw_sa_amt, a.stl_port_code, a.remark, a.curcy_code, ");
			sb.append(" a.exc_date, a.exc_rate_type, a.usd_exc_rate, a.loc_exc_rate,a.usd_loc_rate, ");
			sb.append(" a.due_date, a.terms_date, a.pymt_term, a.pymt_hold_flag ");
			sb.append(" from  EAR_IF_receipt_BALANCE_V d, otc_sa_head c, otc_sa_detail a ");
			sb.append(" where a.sa_no = c.sa_no");
			sb.append(" AND a.STL_ERP_SLIP_NO = d.RECEIPT_NUMBER ");
			sb.append(" and a.sa_no  = " + saNo.longValue() + " ");
			sb.append(" and a.stl_gl_acc_code IN ('210403' ,'210405' ,'210402','210701','210499') ");	//111017 GYJ 210499-기타영업미지급금 추가.)


			log.debug(">> saOwnerSettleApUnSubmitSearch 쿼리문 \n : " + sb.toString() );
			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			OTCSaOwnSettleDTO settleDTO = null;

			while (rs.next()) {
				settleDTO = new OTCSaOwnSettleDTO();
				settleDTO.setCheck_item("1");
				settleDTO.setOp_team_code(Formatter.nullTrim(rs.getString("op_team_code")));
				settleDTO.setCntr_team_code(Formatter.nullTrim(rs.getString("cntr_team_code")));
				settleDTO.setStl_acc_code(Formatter.nullTrim(rs.getString("stl_cntr_acc_code")));
				settleDTO.setStl_acc_name(Formatter.nullTrim(rs.getString("stl_acc_name")));
				settleDTO.setCurrency_code(Formatter.nullTrim(rs.getString("currency_code")));
				settleDTO.setEntered_amt(new Double(rs.getDouble("entered_amt")));
				settleDTO.setUsd_amt(new Double(rs.getDouble("usd_amt")));
				settleDTO.setWon_amt(new Double(rs.getDouble("won_amt")));
				settleDTO.setSlip_no(Formatter.nullTrim(rs.getString("slip_no")));
				settleDTO.setGl_acct(Formatter.nullTrim(rs.getString("gl_acct")));

				settleDTO.setSa_no(new Double(rs.getDouble("sa_no")));
				settleDTO.setStl_flag(Formatter.nullTrim(rs.getString("stl_flag")));
				// stlFlagCk = settleDTO.getStl_flag();
				settleDTO.setStl_vsl_code(Formatter.nullTrim(rs.getString("stl_vsl_code")));
				settleDTO.setStl_voy_no(new Long(rs.getLong("stl_voy_no")));
				settleDTO.setUsd_sa_amt(new Double(rs.getDouble("usd_sa_amt")));
				settleDTO.setLoc_sa_amt(new Double(rs.getDouble("loc_sa_amt")));
				settleDTO.setKrw_sa_amt(new Double(rs.getDouble("krw_sa_amt")));
				settleDTO.setStl_port_code(Formatter.nullTrim(rs.getString("stl_port_code")));
				settleDTO.setStl_erp_slip_no(Formatter.nullTrim(rs.getString("stl_erp_slip_no")));
				settleDTO.setRemark(Formatter.nullTrim(rs.getString("remark")));
				settleDTO.setCurcy_code(Formatter.nullTrim(rs.getString("curcy_code")));
				settleDTO.setExc_date(rs.getTimestamp("exc_date"));
				settleDTO.setExc_rate_type(Formatter.nullTrim(rs.getString("exc_rate_type")));
				settleDTO.setUsd_exc_rate(new Double(rs.getDouble("usd_exc_rate")));
				settleDTO.setLoc_exc_rate(new Double(rs.getDouble("loc_exc_rate")));
				settleDTO.setUsd_loc_rate(new Double(rs.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_usd(new Double(rs.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_krw(new Double(rs.getDouble("loc_exc_rate")));
				//settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs.getTimestamp("exc_date")));
				settleDTO.setExchange_rate_date_krw(rs.getTimestamp("exc_date"));   //RYU
				settleDTO.setDue_date(rs.getTimestamp("due_date"));
				settleDTO.setPymt_term(Formatter.nullTrim(rs.getString("pymt_term")));
				settleDTO.setTerms_date(rs.getTimestamp("terms_date"));
				settleDTO.setGl_date(rs.getTimestamp("gl_date"));
				settleDTO.setPymt_hold_flag(Formatter.nullTrim(rs.getString("pymt_hold_flag")));

				rev.add(settleDTO);

			}

			result.add(rev);
			// **************************** Account payable / Advance Received
			// 가져오기 종료 **************************** //

			// **************************** Account Receivable 가져오기 시작
			// **************************** //
			StringBuffer sb2 = new StringBuffer();

			String trx = " 		 SELECT   /*+ PUSH_PRED(D) */  ";
			trx = trx + "	 			A.STL_CNTR_ACC_CODE,  ";
			trx = trx + "	 			C.OP_TEAM_CODE,   ";
			trx = trx + "	 			C.CNTR_TEAM_CODE,  ";
			trx = trx + "	 			ACC_NAME_FUNC(A.STL_CNTR_ACC_CODE) AS STL_ACC_NAME ,  ";
			trx = trx + "	 			A.CURCY_CODE AS CURRENCY_CODE,  ";
			trx = trx + "	 			(NVL(D.ENTERED_BALANCE_AMOUNT,0)- NVL(D.ENTERED_PENDING_AMOUNT,0)) AS ENTERED_AMT,  ";
			trx = trx + "	 			(NVL(D.USD_BALANCE_AMOUNT,0)- NVL(D.USD_PENDING_AMOUNT,0)) AS USD_AMT,  ";
			trx = trx + "	 			(NVL(D.KRW_BALANCE_AMOUNT,0)- NVL(D.KRW_PENDING_AMOUNT,0)) AS WON_AMT, ";
			trx = trx + "	 			A.STL_ERP_SLIP_NO AS SLIP_NO,  ";
			trx = trx + "	 			A.STL_GL_ACC_CODE AS GL_ACCT,  ";
			trx = trx + "	 			C.SA_NO, A.STL_FLAG, A.STL_VSL_CODE, A.STL_VOY_NO, A.STL_ERP_SLIP_NO, A.USD_SA_AMT,  ";
			trx = trx + "	 			A.LOC_SA_AMT, A.KRW_SA_AMT, A.STL_PORT_CODE, A.REMARK, A.CURCY_CODE,  ";
			trx = trx + "	 			A.EXC_DATE, A.EXC_RATE_TYPE, A.USD_EXC_RATE, A.LOC_EXC_RATE,A.USD_LOC_RATE,  ";
			trx = trx + "	 			A.DUE_DATE, A.TERMS_DATE, A.PYMT_TERM, A.PYMT_HOLD_FLAG,  ";
			trx = trx + "	 			D.TRX_ID,  ";
			trx = trx + "	 			D.TRX_NUMBER,  ";
			trx = trx + "	 			D.GL_DATE,      ";
			trx = trx + "	 			D.SOURCE_SYSTEM,  ";
			trx = trx + "	 			D.SOURCE_TRX_NUMBER,  ";
			trx = trx + "	 			D.SOURCE_TRX_ITEM_NUMBER,  ";
			trx = trx + "	 			D.CURRENCY_CODE,   ";
			trx = trx + "	 			D.ENTERED_TRX_AMOUNT,  ";
			trx = trx + "	 			D.KRW_TRX_AMOUNT,   ";
			trx = trx + "	 			D.USD_TRX_AMOUNT,    ";
			trx = trx + "	 			D.DUE_DATE,           ";
			trx = trx + "	 			D.CUSTOMER_UNIQUE_ID,  ";
			trx = trx + "	 			D.CUSTOMER_NUMBER,      ";
			trx = trx + "	 			D.CUSTOMER_NAME,  ";
			trx = trx + "	 			D.COUNTRY,  ";
			trx = trx + "	 			D.PORT_CODE,  ";
			trx = trx + "	 			D.COMMENTS,  ";
			trx = trx + "	 			D.CREATED_USER,  ";
			trx = trx + "	 			D.SEGMENT1,  ";
			trx = trx + "	 			D.SEGMENT2, ";
			trx = trx + "	 			D.SEGMENT3, ";
			trx = trx + "	 			D.SEGMENT4,  ";
			trx = trx + "	 			D.SEGMENT5,  ";
			trx = trx + "	 			D.SEGMENT6,   ";
			trx = trx + "	 			D.SEGMENT7,   ";
			trx = trx + "	 			D.EXCHANGE_RATE_TYPE_KRW,  ";
			trx = trx + "	 			D.EXCHANGE_RATE_DATE_KRW,  ";
			trx = trx + "	 			D.EXCHANGE_RATE_KRW, ";
			trx = trx + "	 			D.EXCHANGE_RATE_TYPE_USD, ";
			trx = trx + "	 			D.EXCHANGE_RATE_DATE_USD, ";
			trx = trx + "	 			D.EXCHANGE_RATE_USD, ";
			trx = trx + "	 			D.IF_TYPE_ID  ";
			trx = trx + "	 			FROM EAR_IF_TRX_BALANCE_V D, OTC_SA_HEAD C, OTC_SA_DETAIL A  ";
			trx = trx + "	 			WHERE A.SA_NO = C.SA_NO  ";
			trx = trx + "	 			AND A.STL_ERP_SLIP_NO = D.TRX_NUMBER  ";

			sb2.append(trx);
			sb2.append(" and a.sa_no  = " + saNo.longValue() + " ");

			sb2.append(" and a.stl_gl_acc_code IN ('110502' ,'110503', '110599' ) ");			//111017 GYJ 110599-영업미수-기타잔액 추가.



			ps2 = conn.prepareStatement(sb2.toString());
			rs2 = ps2.executeQuery();

			while (rs2.next()) {
				settleDTO = new OTCSaOwnSettleDTO();
				settleDTO.setCheck_item("1");
				settleDTO.setOp_team_code(Formatter.nullTrim(rs2.getString("op_team_code")));
				settleDTO.setCntr_team_code(Formatter.nullTrim(rs2.getString("cntr_team_code")));
				settleDTO.setStl_acc_code(Formatter.nullTrim(rs2.getString("stl_cntr_acc_code")));
				settleDTO.setStl_acc_name(Formatter.nullTrim(rs2.getString("stl_acc_name")));
				settleDTO.setCurrency_code(Formatter.nullTrim(rs2.getString("currency_code")));
				settleDTO.setEntered_amt(new Double(rs2.getDouble("entered_amt")));
				settleDTO.setUsd_amt(new Double(rs2.getDouble("usd_amt")));
				settleDTO.setWon_amt(new Double(rs2.getDouble("won_amt")));
				settleDTO.setSlip_no(Formatter.nullTrim(rs2.getString("slip_no")));
				settleDTO.setGl_acct(Formatter.nullTrim(rs2.getString("gl_acct")));

				settleDTO.setSa_no(new Double(rs2.getDouble("sa_no")));
				settleDTO.setStl_flag(Formatter.nullTrim(rs2.getString("stl_flag")));
				// stlFlagCk = settleDTO.getStl_flag();
				settleDTO.setStl_vsl_code(Formatter.nullTrim(rs2.getString("stl_vsl_code")));
				settleDTO.setStl_voy_no(new Long(rs2.getLong("stl_voy_no")));
				settleDTO.setUsd_sa_amt(new Double(rs2.getDouble("usd_sa_amt")));
				settleDTO.setLoc_sa_amt(new Double(rs2.getDouble("loc_sa_amt")));
				settleDTO.setKrw_sa_amt(new Double(rs2.getDouble("krw_sa_amt")));
				settleDTO.setStl_port_code(Formatter.nullTrim(rs2.getString("stl_port_code")));
				settleDTO.setStl_erp_slip_no(Formatter.nullTrim(rs2.getString("stl_erp_slip_no")));
				settleDTO.setRemark(Formatter.nullTrim(rs2.getString("remark")));
				settleDTO.setCurcy_code(Formatter.nullTrim(rs2.getString("curcy_code")));
				settleDTO.setExc_date(rs2.getTimestamp("exc_date"));
				settleDTO.setExc_rate_type(Formatter.nullTrim(rs2.getString("exc_rate_type")));
				settleDTO.setUsd_exc_rate(new Double(rs2.getDouble("usd_exc_rate")));
				settleDTO.setLoc_exc_rate(new Double(rs2.getDouble("loc_exc_rate")));
				settleDTO.setUsd_loc_rate(new Double(rs2.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_usd(new Double(rs2.getDouble("usd_loc_rate")));
				settleDTO.setExchange_rate_krw(new Double(rs2.getDouble("loc_exc_rate")));
				//settleDTO.setExchange_rate_date_krw(DateUtil.getTimeStampyyyyMMdd10(rs2.getTimestamp("exc_date")));
				settleDTO.setExchange_rate_date_krw(rs2.getTimestamp("exc_date"));  //RYU
				settleDTO.setDue_date(rs2.getTimestamp("due_date"));
				settleDTO.setPymt_term(Formatter.nullTrim(rs2.getString("pymt_term")));
				settleDTO.setTerms_date(rs2.getTimestamp("terms_date"));
				settleDTO.setGl_date(rs2.getTimestamp("GL_DATE"));
				settleDTO.setPymt_hold_flag(Formatter.nullTrim(rs2.getString("pymt_hold_flag")));

				act.add(settleDTO);

			}

			result.add(act);
			// **************************** Account Receivable 가져오기 종료
			// **************************** //



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();
			if (rs2 != null)
				rs2.close();
			if (ps2 != null)
				ps2.close();
			if (rs3 != null)
				rs3.close();
			if (ps3 != null)
				ps3.close();
			if (rs31 != null)
				rs31.close();
			if (ps31 != null)
				ps31.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명: sa StepNo Search을 요청하는 메소드로 business service call하여 결과값을 리턴 받는다.
	 *
	 */
	public boolean saSettleExistSearch(Long saNo, Connection conn) throws STXException {

		boolean result = false;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			StringBuffer sb = new StringBuffer();
			// Query 가져오기
			sb.append(" select count(*) as cnt from otc_sa_detail  where sa_no = ? and  trsact_code between 'I020' and 'I053' ");

			ps = conn.prepareStatement(sb.toString());

			log.debug(">> saSettleExistSearch 쿼리문 \n : " + sb.toString() );
			log.debug(">> saNo.longValue() : " + saNo.longValue() );

			int i = 1;

			ps.setLong(i++, saNo.longValue());
			rs = ps.executeQuery();

			long cnt = 0;

			while (rs.next()) {
				cnt = rs.getLong("cnt");

			}

			if (cnt > 0)
				result = true;

		} catch (Exception e) {
			throw new STXException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e1) {

				throw new STXException(e1);
			}
		}
		return result;
	}

	/**
	 * <p>
	 * 설명: sa brok no를 update한다.
	 *
	 */
	public String saBrokNoModify(String vslCode, Long voyNo, String chtInOutCd, String costFlag, Long stepNo, String trsactCode, String brokerCode, Long brokNo, UserBean userBean, Connection conn) throws Exception, STXException {
		String result = "", chkColumn = "";
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;

		try {

			OTCSAHeadDAO saDao = new OTCSAHeadDAO();
			OTCBrokHeadDTO brkDto = new OTCBrokHeadDTO();

			/*if (chtInOutCd.equals("T")) {
				brkDto = saDao.brokerageIncomeDetailInquiry(vslCode, voyNo, brokerCode,  conn);
			} else {*/
				brkDto = saDao.brokerageTCOutDetailInquiry(vslCode, voyNo, brokerCode, chtInOutCd, conn);
			//}

			chkColumn = brkDto.getInvoice_check();

/*			String brok1 = "";
			String brok2 = "";
			String brokChk = "";

			String brokSql = "SELECT BROK_ACC_CODE, BROK_ACC_CODE2 FROM OTC_CP_ITEM_HEAD WHERE  VSL_CODE = '" + Formatter.nullTrim(vslCode) + "'  AND  VOY_NO = " + Formatter.nullLong(voyNo) + "  AND CHT_IN_OUT_CODE = '" + Formatter.nullTrim(chtInOutCd) + "'  ";

			ps1 = conn.prepareStatement(brokSql);
			rs1 = ps1.executeQuery();

			while (rs1.next()) {

				brok1 = rs1.getString("BROK_ACC_CODE");
				brok2 = rs1.getString("BROK_ACC_CODE2");
			}


			if (Formatter.nullTrim(brok1).equals(Formatter.nullTrim(brokerCode))) {
				brokChk = "1";
			} else if (Formatter.nullTrim(brok2).equals(Formatter.nullTrim(brokerCode))) {
				brokChk = "2";
			}
*/

			if (!"".equals(vslCode)) {

				StringBuffer sb = new StringBuffer();
				// Query 가져오기

				sb.append("	UPDATE OTC_SA_DETAIL a SET    ");
				if ("BROK_NO".equals(Formatter.nullTrim(chkColumn))) {
					sb.append("			a.BROK_NO = ? ,              ");
				} else {
					sb.append("			a.BROK_NO2 = ? ,              ");
				}
				sb.append("		   	a.SYS_UPD_DATE = SYSDATE ,     ");
				sb.append("			a.SYS_UPD_USER_ID = ?              ");
				sb.append("	WHERE    ");
				sb.append("		  a.sa_no = (select b.sa_no from otc_sa_head b where  b.vsl_code = ? and  b.voy_no = ?  and  b.cht_in_out_code = ? and  b.step_no = ? )    ");
				sb.append("		  and a.trsact_code = ?     ");

				if("PROFIT".equals(Formatter.nullTrim(costFlag))) {  // 정보이용수수료의 경우는 해당 broker로 reserved한 내역에 대해서만 번호 세팅함
					sb.append("		  and a.brok_acc_code = ?     ");  // brokerage의 경우는 on-hire ~ speed claim 등의 항목에 대해서 번호 세
				}

				ps = conn.prepareStatement(sb.toString());

				int i = 1;
				ps.setLong(i++, Formatter.nullLong(brokNo));
				ps.setString(i++, Formatter.nullTrim(userBean.getUser_id()));

				ps.setString(i++, Formatter.nullTrim(vslCode));
				ps.setLong(i++, Formatter.nullLong(voyNo));
				ps.setString(i++, Formatter.nullTrim(chtInOutCd));
				ps.setLong(i++, Formatter.nullLong(stepNo));
				ps.setString(i++, Formatter.nullTrim(trsactCode));

				if("PROFIT".equals(Formatter.nullTrim(costFlag))) {
					ps.setString(i++, Formatter.nullTrim(brokerCode));
				}

				ps.executeUpdate();

				result = "SUC-0600";

			}

		} catch (Exception e) {

			throw new STXException(e);
		} finally {
			try {
				if (rs1 != null)
					rs1.close();
				if (ps1 != null)
					ps1.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e1) {
				throw new STXException(e1);
			}
		}
		return result;
	}

	/**
	 * <p>
	 * 설명: sa brok no를 초기화한다.
	 *
	 */
	public String saBrokNoCancel(Long brokNo, String brokerCode, String vslCode, Long voyNo, String chtInCode, UserBean userBean, Connection conn) throws Exception, STXException {
		String result = "", chkColumn = "";
		PreparedStatement ps = null;

		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {

			OTCSAHeadDAO saDao = new OTCSAHeadDAO();
			OTCBrokHeadDTO brkDto = new OTCBrokHeadDTO();

			if (chtInCode.equals("T")) {
				brkDto = saDao.brokerageIncomeDetailInquiry(vslCode, voyNo, brokerCode,  conn);
			} else {
				brkDto = saDao.brokerageTCOutDetailInquiry(vslCode, voyNo, brokerCode,  chtInCode, conn);
			}

			chkColumn = brkDto.getInvoice_check();
/*
			String brok1 = "";
			String brok2 = "";
			String brokChk = "";

			String brokSql = "SELECT BROK_ACC_CODE, BROK_ACC_CODE2 FROM OTC_CP_ITEM_HEAD WHERE  VSL_CODE = '" + Formatter.nullTrim(vslCode) + "'  AND  VOY_NO = " + Formatter.nullLong(voyNo) + "  AND CHT_IN_OUT_CODE = '" + Formatter.nullTrim(chtInCode) + "'  ";

			ps1 = conn.prepareStatement(brokSql);
			rs1 = ps1.executeQuery();

			while (rs1.next()) {

				brok1 = rs1.getString("BROK_ACC_CODE");
				brok2 = rs1.getString("BROK_ACC_CODE2");
			}

			if (Formatter.nullTrim(brok1).equals(Formatter.nullTrim(brokerCode))) {
				brokChk = "1";
			} else if (Formatter.nullTrim(brok2).equals(Formatter.nullTrim(brokerCode))) {
				brokChk = "2";
			} */

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append("		UPDATE OTC_SA_DETAIL a SET 				");
			if ("BROK_NO".equals(Formatter.nullTrim(chkColumn))) {

				sb.append("				a.BROK_NO = ? ,  				");
			} else {
				sb.append("				a.BROK_NO2 = ? ,  				");
			}
			sb.append("			   	a.SYS_UPD_DATE = SYSDATE ,      ");
			sb.append("				a.SYS_UPD_USER_ID = ? 			");
			sb.append("		WHERE 									");
			if ("BROK_NO".equals(Formatter.nullTrim(chkColumn))) {

				sb.append("			 a.brok_no = ? 						");
			} else {
				sb.append("			 a.brok_no2 = ? 					");
			}

			ps = conn.prepareStatement(sb.toString());

			log.debug("saBrokNoCancel ryu : " + sb.toString());

			int i = 1;
			ps.setLong(i++, 0);
			ps.setString(i++, Formatter.nullTrim(userBean.getUser_id()));

			ps.setLong(i++, Formatter.nullLong(brokNo));
			ps.executeUpdate();

			result = "SUC-0600";

		} catch (Exception e) {
			throw new STXException(e);
		} finally {
			try {
				if (rs1 != null)
					rs1.close();
				if (ps1 != null)
					ps1.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e1) {
				throw new STXException(e1);
			}
		}
		return result;
	}

	/**
	 * <p>
	 * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 Withholding Tax 정보를 읽어온다
	 * chtInOutCOde : O, T, R
	 */
	public long saWithholdingCheckSearch(Long saNo, String wthFlag, Connection conn) throws Exception, STXException {

		long result = 0;


		PreparedStatement ps1 = null;
		ResultSet rs1 = null;

		try {

			// **************************** Withholding Tax check가져오기 시작
			// **************************** //
			if ("Y".equals(wthFlag)) {

				StringBuffer sb1 = new StringBuffer();

				sb1.append(" SELECT count(*)  as cnt   FROM OTC_SA_DETAIL V WHERE V.SA_NO = ?  AND   V.trsact_code IN ('M001', 'M002')  ");



				ps1 = conn.prepareStatement(sb1.toString());
				ps1.setLong(1, Formatter.nullLong(saNo));
				rs1 = ps1.executeQuery();

				while (rs1.next()) {

					result = rs1.getLong("cnt");

				} // rs while
				// **************************** Withholding Tax check 가져오기 종료
				// **************************** //

			}



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 세금계산서를 발행할 정보가 있는지 읽어온다.
	 * chtInOutCOde : O, T, R
	 */
	public long saVatExistCheckSearch(Long saNo, Connection conn) throws Exception, STXException {

		long result = 0;


		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {

			StringBuffer sb1 = new StringBuffer();

			String detailSql = " SELECT count(*)  as cnt   FROM OTC_SA_DETAIL V WHERE V.SA_NO = ? AND V.VAT_FLAG = 'Y' ";
			sb1.append(detailSql);



			ps1 = conn.prepareStatement(sb1.toString());
			ps1.setLong(1, Formatter.nullLong(saNo));
			rs1 = ps1.executeQuery();

			while (rs1.next()) {

				result = rs1.getLong("cnt");

			} // rs while



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명: sa 관리화면 ONLOAD시 Search을 요청하는 메소드로 business service call하여 결과값을 리턴
	 * 받는다.
	 *
	 * @return Collection : 이전 스텝의 hire값을 가져온다.
	 *
	 */
	public Collection saPrestephireSearch(Long saNo, Connection conn) throws STXException {

		Collection result = null;
		try {


			// dao 객체 생성
			OTCSADetailDAO dao = new OTCSADetailDAO();

			if (saNo != null) {
				result = dao.saOnHireSelect(saNo, null, "", conn);  //RYU TO-DO
			}


		} catch (Exception e) {
			throw new STXException(e);
		}
		return result;

	}

	/**
	 * <p>
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 */
	public OTCSaOffHireDTO saOffhireBunkerPriceInitSelect(String vslCode, Long voyNo, String chtInCode, Timestamp fromHire, Timestamp toHire, Connection conn) throws STXException, Exception {

		OTCSaOffHireDTO result = new OTCSaOffHireDTO();


		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			// **************************** Off Hire 가져오기 시작
			// **************************** //

			StringBuffer sb = new StringBuffer();

			sb.append(" SELECT A.*, ACC_NATION_FUNC(A.CNTR_ACC_CODE) AS ACC_NAT_CODE, B.CP_DATE, B.FROM_DATE, B.TO_DATE, B.DAY_HIRE, B.HIRE_DUR, B.ADDR_COMM_RATE ");
			sb.append("			FROM OTC_CP_ITEM_HEAD A, OTC_CP_ITEM_DETAIL B ");
			sb.append("			WHERE A.CP_ITEM_NO = B.CP_ITEM_NO ");
			sb.append("			  AND A.VSL_CODE = ? ");
			sb.append("			  AND A.VOY_NO = ?   ");
			sb.append("			  AND A.CHT_IN_OUT_CODE = ? ");
			sb.append("			  AND B.CP_ITEM_SEQ = (SELECT MIN(CP_ITEM_SEQ) ");
			sb.append("			  					   FROM OTC_CP_ITEM_DETAIL        ");
			sb.append("			  					   WHERE CP_ITEM_NO = A.CP_ITEM_NO) ");

			int i = 1;

			ps = conn.prepareStatement(sb.toString());
			ps.setString(i++, vslCode);
			ps.setLong(i++, voyNo.longValue());
			ps.setString(i++, chtInCode);



			rs = ps.executeQuery();

			if (rs.next()) {

				result.setDay_hire(new Double(rs.getDouble("DAY_HIRE")));
				result.setAdd_comm(new Double(rs.getDouble("ADDR_COMM_RATE")));
				result.setFo_price(new Double(rs.getDouble("FO_PRICE")));
				result.setDo_price(new Double(rs.getDouble("DO_PRICE")));
				result.setFo_idle(new Double(rs.getDouble("FO_IDLE")));
				result.setDo_idle(new Double(rs.getDouble("DO_IDLE")));

			} // while

			// **************************** Off Hire 가져오기 종료
			// **************************** //


		} catch (Exception e) {

			throw new STXException(e);
		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail Hire내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public OTCSaSpeedClaimDTO saOwnerACBunkerInitSearch(String vslCode, Long voyNo, String chtinCd, Connection conn) throws STXException, Exception {

		OTCSaSpeedClaimDTO result = null;


		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer();

			sb.append(" SELECT               ");
			sb.append("  				A.CP_ITEM_NO,  ");
			sb.append("  				A.BLST_BONUS,   ");
			sb.append("  				A.CVE,          ");
			sb.append("  				A.VOY_NO ,      ");
			sb.append("  				A.APLY_TIME_FLAG ,  ");
			sb.append("  				A.CNTR_NO  ,        ");
			sb.append("  				A.LAY_CAN_FROM_DATE  , ");
			sb.append("  				A.CNTR_ACC_CODE ,   ");
			sb.append("  				A.LAY_CAN_TO_DATE,  ");
			sb.append("  				A.BANK_NAT_CODE,    ");
			sb.append("  				A.OP_TEAM_CODE ,   ");
			sb.append("  				A.WTH_TAX_FLAG ,   ");
			sb.append("  				A.REDLY_NOTICE1 ,   ");
			sb.append("  				A.BROK_COMM_RATE  ,  ");
			sb.append("  				A.REDLY_NOTICE2,    ");
			sb.append("  				A.BROK_COMM_RATE2 , ");
			sb.append("  				A.REDLY_NOTICE3 ,   ");
			sb.append("  				A.FO_PRICE ,         ");
			sb.append("  				A.REDLY_NOTICE4,    ");
			sb.append("  				A.ILOHC   ,         ");
			sb.append("  				A.REDLY_NOTICE5  ,  ");
			sb.append("  				A.CHT_IN_OUT_CODE,  ");
			sb.append("  				A.REDLY_NOTICE6,    ");
			sb.append("  				A.NAT_CODE,         ");
			sb.append("  				A.REDLY_NOTICE7,    ");
			sb.append("  				A.BROK_ACC_CODE ,   ");
			sb.append("  				A.REDLY_NOTICE8 ,   ");
			sb.append("  				A.ADTNL_COMM_RATE , ");
			sb.append("  				A.REDLY_NOTICE9,     ");
			sb.append("  				A.VSL_CODE,          ");
			sb.append("  				A.REDLY_NOTICE10,    ");
			sb.append("  				A.DO_PRICE ,         ");
			sb.append("  				A.CNTR_TEAM_CODE ,   ");
			sb.append("  				A.BROK_ACC_CODE2,    ");
			sb.append("  				A.SYS_CRE_DATE,     ");
			sb.append("  				A.SYS_CRE_USER_ID , ");
			sb.append("  				A.SYS_UPD_DATE,     ");
			sb.append("			    	A.SYS_UPD_USER_ID ,    ");
			sb.append("  				A.BANK_NAME, ");
			sb.append("				NAT_ENG_NAME_FUNC(A.BANK_NAT_CODE) AS BANK_NAT_NAME,		");
			sb.append("				NAT_ENG_NAME_FUNC(A.NAT_CODE) AS NAT_NAME,");
			sb.append("				TEAM_INFO_FUNC(A.OP_TEAM_CODE) AS OP_TEAM_NAME, ");
			sb.append("				TEAM_INFO_FUNC(A.CNTR_TEAM_CODE) AS CNTR_TEAM_NAME, ");
			sb.append("             ACC_NAME_FUNC(A.CNTR_ACC_CODE) AS CNTR_ACC_NAME,");
			sb.append("				ACC_NAME_FUNC(A.BROK_ACC_CODE) AS BROK_ACC_NAME, ");
			sb.append("				ACC_NAME_FUNC(A.BROK_ACC_CODE2) AS BROK_ACC_NAME2,");
			sb.append("				VSL_NAME_FUNC(A.VSL_CODE) AS VSL_NAME, ");
			sb.append("				Cntr_Name_Func(A.CNTR_NO) AS CNTR_NAME, ");
			sb.append("				ACC_NATION_FUNC(A.CNTR_ACC_CODE) AS ACC_NAT_CODE, ");
			sb.append("			    NAT_ENG_NAME_FUNC(ACC_NATION_FUNC(A.CNTR_ACC_CODE)) AS ACC_NAT_NAME ");
			sb.append("			  FROM  OTC_CP_ITEM_HEAD A ");
			sb.append("			  WHERE  ");
			sb.append("           A.VSL_CODE = '" + vslCode + "' ");
			sb.append("           AND A.VOY_NO = " + voyNo.longValue() + " ");
			sb.append("           AND A.CHT_IN_OUT_CODE = '" + chtinCd + "' ");



			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			double add_comm = 0;
			double fo_prc = 0;
			double do_prc = 0;
			while (rs.next()) {

				add_comm = rs.getDouble("ADTNL_COMM_RATE");
				fo_prc = rs.getDouble("FO_PRICE");
				do_prc = rs.getDouble("DO_PRICE");

			}

			result = new OTCSaSpeedClaimDTO();
			result.setAdd_comm(new Double(add_comm));
			result.setFo_price(new Double(fo_prc));
			result.setDo_price(new Double(do_prc));
			result.setFactor(new Double(100));



		} catch (Exception e) {

			throw new STXException(e);
		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명: sa 세금계산서 번호를 update한다.
	 *
	 */
	public String saVatNoModify(Long saNo, Long vatNo, String trsactCd, UserBean userBean, Connection conn) throws Exception, STXException {
		String result = "";
		PreparedStatement ps = null;
		try {

			if (saNo != null) {

				StringBuffer sb = new StringBuffer();
				// Query 가져오기

				sb.append("		UPDATE OTC_SA_DETAIL SET ");
				sb.append("			VAT_NO = ? ,           ");
				sb.append("		 	SYS_UPD_DATE = SYSDATE , ");
				sb.append("			SYS_UPD_USER_ID = ? ");
				sb.append("		 WHERE SA_NO = ?  ");
				sb.append("		 AND TRSACT_CODE = ? ");

				ps = conn.prepareStatement(sb.toString());

				int i = 1;
				ps.setLong(i++, Formatter.nullLong(vatNo));
				ps.setString(i++, Formatter.nullTrim(userBean.getUser_id()));

				ps.setLong(i++, Formatter.nullLong(saNo));
				ps.setString(i++, Formatter.nullTrim(trsactCd));

				ps.executeUpdate();

				result = "SUC-0600";

			}

		} catch (Exception e) {

			throw new STXException(e);
		} finally {
			try {

				if (ps != null)
					ps.close();
			} catch (SQLException e1) {

				throw new STXException(e1);
			}
		}
		return result;
	}

	public String saVatNoModify(OTCSaDetailVO saVO, UserBean userBean, Connection conn) throws Exception, STXException {
		String result = "";
		PreparedStatement ps = null;
		try {

			if (saVO.getSa_no() != null) {

				StringBuffer sb = new StringBuffer();
				// Query 가져오기

				sb.append("		UPDATE OTC_SA_DETAIL SET ");
				sb.append("		    VAT_NO = ? ,           ");
				sb.append("		    CONTACT_ID_1 = ? ,           ");
				sb.append("		    CONTACT_ID_2 = ? ,           ");
				sb.append("		    NTS_APPROVE_NUMBER = ? ,       ");
				sb.append("		 	SYS_UPD_DATE = SYSDATE , ");
				sb.append("			SYS_UPD_USER_ID = ? ");
				sb.append("		 WHERE SA_NO = ?  ");
				sb.append("		 AND TRSACT_CODE = ? ");
				sb.append("		 AND TAX_CODE_FLAG = ? ");
				sb.append("		 AND VAT_FLAG = 'Y' ");

				if (Formatter.nullLong(saVO.getGroup_seq()) != 0) {
					sb.append("		 AND GROUP_SEQ = ? ");
				}

				ps = conn.prepareStatement(sb.toString());

				int i = 1;
				ps.setLong(i++, Formatter.nullLong(saVO.getVat_no()));
				ps.setLong(i++, Formatter.nullLong(saVO.getContact_id_1()));
				ps.setLong(i++, Formatter.nullLong(saVO.getContact_id_2()));
				ps.setString(i++, Formatter.nullTrim(saVO.getNts_approve_number()));

				ps.setString(i++, Formatter.nullTrim(userBean.getUser_id()));

				ps.setLong(i++, Formatter.nullLong(saVO.getSa_no()));
				ps.setString(i++, Formatter.nullTrim(saVO.getTrsact_code()));
				ps.setString(i++, Formatter.nullTrim(saVO.getTax_code_flag()));

				if (Formatter.nullLong(saVO.getGroup_seq()) != 0) {
					ps.setLong(i++, Formatter.nullLong(saVO.getGroup_seq()));
				}

				ps.executeUpdate();

				result = "SUC-0600";

			}

		} catch (Exception e) {

			throw new STXException(e);
		} finally {
			try {

				if (ps != null)
					ps.close();
			} catch (SQLException e1) {

				throw new STXException(e1);
			}
		}
		return result;

	}

	/**
	 * <p>
	 * 설명:sa 매출 세금계산서 발행 갯수를 조회하는 메소드이다.
	 *
	 * @param sa
	 *            no
	 * @return sa sale vat count
	 * @exception STXException :
	 */
	public Long saSaleVatSelect(Long saNo, Connection conn) throws STXException {

		Long result = null;


		PreparedStatement ps = null;
		ResultSet rs = null;
		long cnt = 0;
		try {

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT ");
			sb.append(" count(*) as cnt ");
			sb.append(" FROM OTC_SA_DETAIL A  ");
			sb.append(" WHERE A.SA_NO = " + Formatter.nullLong(saNo) + " ");
			sb.append(" AND A.TAX_CODE_ID in  ('VS111101'  ,'VS121101')  ");
			sb.append(" AND A.VAT_FLAG = 'Y'  ");

			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			if (rs.next()) {
				cnt = rs.getLong("cnt");

			}

			result = new Long(cnt);

		} catch (Exception e) {

			throw new STXException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e1) {

				throw new STXException(e1);
			}
		}

		return result;

	}

	/**
	 * <p>
	 * 설명:sa 매입 세금계산서 발행 갯수를 조회하는 메소드이다.
	 *
	 * @param sa
	 *            no
	 * @return sa sale vat count
	 * @exception STXException :
	 */
	public Long saPurchaseVatSelect(Long saNo, Connection conn) throws STXException {

		Long result = null;


		PreparedStatement ps = null;
		ResultSet rs = null;
		long cnt = 0;
		try {

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT ");
			sb.append(" count(*) as cnt ");
			sb.append(" FROM OTC_SA_DETAIL A  ");
			sb.append(" WHERE A.SA_NO = " + Formatter.nullLong(saNo) + " ");
			sb.append(" AND A.TAX_CODE_ID in  ('VP111101'  ,'VP121101')  ");
			sb.append(" AND A.VAT_FLAG = 'Y'  ");

			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			if (rs.next()) {
				cnt = rs.getLong("cnt");

			}

			result = new Long(cnt);

		} catch (Exception e) {

			throw new STXException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e1) {

				throw new STXException(e1);
			}
		}

		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail 내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public OTCSaDetailDTO saDetailValidate(Long saNo, Connection conn) throws STXException {

		OTCSaDetailDTO result = null;


		try {
			DbWrap dbWrap = new DbWrap();

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append("		SELECT count(*) cnt from otc_sa_detail where sa_no = " + saNo.longValue() + " ");

			result = (OTCSaDetailDTO) dbWrap.getObject(conn, OTCSaDetailDTO.class, sb.toString());

		} catch (Exception e) {

			throw new STXException(e);
		}
		return result;
	}

	/**
	 * <p>
	 * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 Withholding Tax 정보를 읽어온다
	 * chtInOutCOde : O, T, R
	 */
	public long saWithholdingCheckSearch(Long saNo, Connection conn) throws Exception, STXException {
		long result = 0;


		PreparedStatement ps1 = null;
		ResultSet rs1 = null;

		try {

			StringBuffer sb1 = new StringBuffer();

			sb1.append(" SELECT COUNT(*)  CNT  FROM OTC_SA_DETAIL V ");
			sb1.append(" WHERE V.SA_NO = " + saNo.longValue() + " ");
			sb1.append(" AND  V.trsact_code IN ('M001', 'M002') ");



			ps1 = conn.prepareStatement(sb1.toString());
			rs1 = ps1.executeQuery();

			while (rs1.next()) {

				result = rs1.getLong("CNT");

			} // rs while



		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();

		}
		return result;
	}

	/**
	 * <p>
	 * 설명:sa AP Detail 내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public OTCSaDetailVO saAPDetailSelect(Long saNo, Connection conn) throws STXException {

		OTCSaDetailVO result = null;


		try {
			DbWrap dbWrap = new DbWrap();

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append("		SELECT  V.*   ");
			sb.append("	          FROM OTC_SA_DETAIL V    ");
			sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND TRSACT_CODE = 'L001' ");

			result = (OTCSaDetailVO) dbWrap.getObject(conn, OTCSaDetailVO.class, sb.toString());

		} catch (Exception e) {

			throw new STXException(e);
		}
		return result;
	}

	/**
	 * <p>
	 * 설명: 거래처의 미결 금액 정보가 존재한는 항차를 조회한다. chtInOutCOde : O, T, R
	 */
	public Collection openVslSearch(String accCode, String vslCode, Long voyNo, Connection conn) throws Exception, STXException {

		Collection result = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		SCBVslVoyMDTO voyDTO = null;


		try {
			String acc_code = Formatter.nullTrim(accCode);
			String vessel = Formatter.nullTrim(vslCode);
			long voy = Formatter.nullLong(voyNo);
			StringBuffer sb = new StringBuffer();

			sb.append(" select distinct segment4 as vsl_code ,vsl_name_func(segment4) as vsl_name , segment5 as voy_no ");
			sb.append(" from  ear_if_invoice_balance_v   ");
			sb.append(" where customer_unique_id = '" + acc_code + "' ");
			sb.append(" and 	 (nvl(usd_balance_amount,0)- nvl(usd_pending_amount,0))  > 0  ");
			if (!"".equals(vessel)) {
				sb.append(" and segment4 = '" + vessel + "'   ");
			}
			if (voy != 0) {
				sb.append(" and segment5 =  '" + String.valueOf(voy) + "'   ");
			}
			sb.append(" union all  ");
			sb.append(" select distinct segment4 as vsl_code ,vsl_name_func(segment4) as vsl_name , segment5 as voy_no  ");
			sb.append(" from  ear_if_trx_balance_v  ");
			sb.append(" where customer_unique_id = '" + acc_code + "'  ");
			sb.append(" and 	 (nvl(usd_balance_amount,0)- nvl(usd_pending_amount,0))  > 0  ");

			if (!"".equals(vessel)) {
				sb.append(" and segment4 = '" + vessel + "'   ");
			}
			if (voy != 0) {
				sb.append(" and segment5 = '" + String.valueOf(voy) + "'   ");
			}

			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();
			int row = 0;
			while (rs.next()) {
				row = row + 1;
				if (row == 1)
					result = new ArrayList();
				voyDTO = new SCBVslVoyMDTO();
				voyDTO.setVsl_code(rs.getString("VSL_CODE"));
				voyDTO.setVsl_name(rs.getString("VSL_NAME"));
				voyDTO.setVoy_no(Long.valueOf(rs.getString("VOY_NO")));
				result.add(voyDTO);
			}



		} catch (Exception e) {

			throw new STXException(e);

		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;

	}

	/**
	 * <p>
	 * 설명:sa Detail 내역을 조회하는 메소드이다.
	 *
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public OTCSaDetailDTO saDetailValidateTax(Long saNo, Connection conn) throws STXException {

		OTCSaDetailDTO result = new OTCSaDetailDTO();

		PreparedStatement ps = null;
		ResultSet rs = null;

		PreparedStatement ps1 = null;
		ResultSet rs1 = null;

		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		long cnt1 = 0;
		long cnt2 = 0;

		try {

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append("		SELECT count(*) cnt from otc_sa_detail where sa_no = " + saNo.longValue() + " ");

			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			// Query 가져오기

			sb1.append("		SELECT count(*) cnt1 from otc_sa_detail where sa_no = " + saNo.longValue() + " and tax_code_id in ( 'VP111101', 'VP121101') ");

			// Query 가져오기

			sb2.append("		SELECT count(*) cnt2 from otc_sa_detail where sa_no = " + saNo.longValue() + " and tax_code_id in ( 'VS111101', 'VS121101') ");

			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			if (rs.next()) {

				result.setCnt(new Long(rs.getLong("cnt")));
			}

			ps1 = conn.prepareStatement(sb1.toString());
			rs1 = ps1.executeQuery();

			if (rs1.next()) {

				cnt1 = rs1.getLong("cnt1");
			}

			if (cnt1 > 0)
				result.setPur_tax_flag("Y");

			ps2 = conn.prepareStatement(sb2.toString());
			rs2 = ps2.executeQuery();

			if (rs2.next()) {

				cnt2 = rs2.getLong("cnt2");
			}
			if (cnt2 > 0)
				result.setSale_tax_flag("Y");

		} catch (Exception e) {

			throw new STXException(e);
		} finally {

			try {
				if (rs != null)
					rs.close();
				if (rs1 != null)
					rs1.close();
				if (rs2 != null)
					rs2.close();
				if (ps != null)
					ps.close();
				if (ps1 != null)
					ps1.close();
				if (ps2 != null)
					ps2.close();

			} catch (SQLException e) {

				throw new STXException(e);
			}

		}
		return result;
	}



	/**
	 * <p>
	 * 설명: 입력조건에 해당하는 sa정보가 있으면 해당 조건에 해당하는 sa head밑 add commition 정보를 읽어온다
	 * chtInOutCOde : O, T, R
	 */
	public OTCOwnersComDTO saAddCommSearch(String vslCode, Long voyNo, String chtInOutCode, Long saNo, Connection conn)  throws STXException {



		OTCOwnersComDTO result = new OTCOwnersComDTO();

		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		PreparedStatement ps11 = null;
		ResultSet rs11 = null;
		PreparedStatement ps21 = null;
		ResultSet rs21 = null;

		try {



			// ADD COMM 가져오기
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT  V.SA_NO,V.VAT_FLAG, V.ORG_VAT_NO, V.SA_RATE, V.USD_SA_AMT, V.USD_VAT_SA_AMT, V.KRW_SA_AMT,  V.KRW_VAT_SA_AMT, V.VOY_NO    " );
			sb.append(" FROM OTC_SA_DETAIL V ");
			sb.append(" WHERE V.SA_NO = "+Formatter.nullLong(saNo)+"   AND   V.trsact_code = 'I054'  ");

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			if (rs.next()) {

				result.setVat_flag(Formatter.nullTrim(rs.getString("VAT_FLAG")));
				result.setOrg_vat_no(new Long(rs.getLong("ORG_VAT_NO")));
				result.setAdd_comm(new Double(rs.getDouble("SA_RATE")));
				result.setAdd_comm_usd_amt(new Double(rs.getDouble("USD_SA_AMT")));
				result.setAdd_comm_usd_vat_amt(new Double(rs.getDouble("USD_VAT_SA_AMT")));
				result.setAdd_comm_krw_amt(new Double(rs.getDouble("KRW_SA_AMT")));
				result.setAdd_comm_krw_vat_amt(new Double(rs.getDouble("KRW_VAT_SA_AMT")));
				result.setVoyage(new Long(rs.getLong("VOY_NO")));     // 채산 항차 추가 ryu 20100203
			}

			//RESERVED
			StringBuffer sb1 = new StringBuffer();
			sb1.append(" SELECT   sum(V.USD_SA_AMT)  as USD_SA_AMT" );
			sb1.append(" FROM  OTC_SA_DETAIL V ");
			sb1.append(" WHERE V.SA_NO = "+Formatter.nullLong(saNo)+"  ");
			sb1.append(" AND V.TRSACT_CODE IN ( 'I020', 'I021')  ");

			ps1 = conn.prepareStatement(sb1.toString());
			rs1 = ps1.executeQuery();

			if (rs1.next()) {

				result.setRsv_usd_amt(new Double(rs1.getDouble("USD_SA_AMT")));

			}


			//AP
			StringBuffer sb11 = new StringBuffer();
			sb11.append(" SELECT   sum(V.USD_SA_AMT)  as USD_SA_AMT  " );
			sb11.append(" FROM  OTC_SA_DETAIL V ");
			sb11.append(" WHERE V.SA_NO = "+Formatter.nullLong(saNo)+"  ");
			sb11.append(" AND V.TRSACT_CODE IN ( 'I050','I051' )  ");


			ps11 = conn.prepareStatement(sb11.toString());
			rs11 = ps11.executeQuery();

			if (rs11.next()) {

				result.setAp_usd_amt(new Double(rs11.getDouble("USD_SA_AMT")));

			}


			//owner's ac
			StringBuffer sb2 = new StringBuffer();
			sb2.append(" SELECT   sum(V.USD_SA_AMT)  as USD_SA_AMT  " );
			sb2.append(" FROM  OTC_SA_DETAIL V ");
			sb2.append(" WHERE V.SA_NO = "+Formatter.nullLong(saNo)+"  ");
			sb2.append(" AND V.TRSACT_CODE IN ( 'I030' ,'I031'  )  ");


			ps2 = conn.prepareStatement(sb2.toString());
			rs2 = ps2.executeQuery();

			if (rs2.next()) {

				result.setOwnac_usd_amt(new Double(rs2.getDouble("USD_SA_AMT")));

			}

			//ar
			StringBuffer sb21 = new StringBuffer();
			sb21.append(" SELECT   sum(V.USD_SA_AMT)  as USD_SA_AMT  " );
			sb21.append(" FROM  OTC_SA_DETAIL V ");
			sb21.append(" WHERE V.SA_NO = "+Formatter.nullLong(saNo)+"  ");
			sb21.append(" AND V.TRSACT_CODE IN ( 'I040','I041'  )  ");


			ps21 = conn.prepareStatement(sb21.toString());
			rs21 = ps21.executeQuery();

			if (rs21.next()) {

				result.setAr_usd_amt(new Double(rs21.getDouble("USD_SA_AMT")));

			}


			//total value
			result.setRsv_ttl_usd_amt(new Double(Formatter.nullDouble(result.getRsv_usd_amt()) + Formatter.nullDouble(result.getAp_usd_amt())));
			//total value
			result.setOwnac_ttl_usd_amt(new Double(Formatter.nullDouble(result.getOwnac_usd_amt()) + Formatter.nullDouble(result.getAr_usd_amt())));




		} catch (Exception e) {

			throw new STXException(e);
		} finally {

			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();

				if (rs1 != null)
					rs1.close();
				if (ps1 != null)
					ps1.close();

				if (rs2 != null)
					rs2.close();
				if (ps2 != null)
					ps2.close();

				if (rs11 != null)
					rs11.close();
				if (ps11 != null)
					ps11.close();

				if (rs21 != null)
					rs21.close();
				if (ps21 != null)
					ps21.close();


			} catch (SQLException e) {

				throw new STXException(e);
			}

		}
		return result;
	}


	/**
	 * <p>
	 * 설명:sa bunker init내역을 조회하는 메소드이다.
	 *
	 */
	public boolean saSettleCheck(Long sa_no,Connection conn) throws STXException, Exception {

		boolean result = false;


		PreparedStatement ps = null;
		ResultSet rs = null;
		int cnt = 0;
		try {

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append("	SELECT count(*) as cnt   ");
			sb.append("			FROM OTC_SA_DETAIL   ");
			sb.append("			WHERE SA_NO = "+Formatter.nullLong(sa_no)+"         ");
			sb.append("			AND TRSACT_CODE IN ('I020' ,'I021','I030','I031','I032','I033','I034' ,'I040','I041','I022','I050','I051','I052','I053','I054' )        ");


			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();


			if (rs.next()) {

					cnt = rs.getInt("cnt");
			}

			if(cnt > 0 ) result = true;


		} catch (Exception e) {

			throw new STXException(e);

		} finally {

			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
		return result;
	}


	/**
	 * <p>
	 * 설명: 용선 원천징수 대상여부 검사
	 */
	public String saWithTaxCheck(String vslCode, Long voyNo, String chtInOutCode, Long stepNo, UserBean userBean, Connection conn) throws Exception, STXException, RemoteException {

		String result = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		long saCnt = 0;

		log.debug(" [saWithTaxCheck DAO Start!!]");

		StringBuffer sb = new StringBuffer();
		try {
			sb.append("	select count(b.sa_no) saCnt	from otc_sa_detail b								\n");
			sb.append("	where b.sa_no in (																\n");
			sb.append("		select a.sa_no	from otc_sa_head a, otc_sa_detail b							\n");
			sb.append("		where a.sa_no = b.sa_no														\n");
			sb.append("		and a.vsl_code = ?     														\n");
			sb.append("		and a.voy_no = ?        													\n");
			sb.append("		and a.cht_in_out_code = ?  													\n");
			sb.append("		and a.step_no = ?   														\n");
			sb.append("		and a.wth_flag = 'Y'														\n");
			//sb.append("		and b.TRSACT_CODE in ('A001','A002','G001','G002','H001','H002','H005','I003','I004','I005')	\n");
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
			//sb.append("		and b.TRSACT_CODE in ('A001','A002','A006','A007', 'G001','G002','G003','G004', 'H001','H002','H009','H010', 'H005','H011', 'I003','I004','I005','I071','I072','I073')	\n");
			//위 REMARK 230309 GYJ
			//SPD CLAIM ACTUAL 생성+원천징수 생성 이후 SPD CLAIM RESERVE로 변경하게 되면 기존 원천징수 인식하지 못하여 화면에서 삭제 여부 묻지 않아 ACTUAL당시 BASE 금액이 그대로 신고되는 문제 발생.
			//하여 재경 이경화 협의하여 무엇을 저장하든 원천징수 자료가 해당 전표번호로 생성되어 있는 경우 자동으로 삭제할 수 있도록 함.
			sb.append("	)																				\n");
			sb.append("	and b.TRSACT_CODE in ('M001','M002','M004')										\n");

			log.debug("vslCode:"+vslCode);
			log.debug("voyNo:"+voyNo.toString());
			log.debug("chtInOutCode:"+chtInOutCode);
			log.debug("stepNo:"+stepNo.toString());

			log.debug(" [saWithTaxCheck query]"+sb.toString());

			ps = conn.prepareStatement(sb.toString());
			int i = 1;
			ps.setString(i++, vslCode);
			ps.setLong(i++, voyNo.longValue());
			ps.setString(i++, chtInOutCode);
			ps.setLong(i++, stepNo.longValue());

			rs = ps.executeQuery();
			while (rs.next()) {
				saCnt = rs.getLong("saCnt");
			}

			if(saCnt > 0){
				result = "Y";
			}else{
				result = "N";
			}
		} catch (Exception e) {
			throw new STXException(e);
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
		log.debug("[saWithTaxCheck DAO result]:"+result);
		return result;
	}

	/**
	 * <p>
	 * 설명: 용선 원천징수 저장
	 */
	public String saWithTaxInvoiceInsert(String vslCode, Long voyNo, String chtInOutCode, Long stepNo,
				OTCSaWithholdingTaxDTO otcSaWithInfo, UserBean userBean, Connection conn) throws Exception, STXException {

		String result = "", 	vslOwnCode = "", cht_in_out_code = "", gl_acc_code = "", evidence_flag = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringBuffer sb = new StringBuffer();
			// sa seq max 값 가져오기
			OTCSADetailDAO dao = new OTCSADetailDAO();

			//conn.setAutoCommit(false);

			CCDVslCodeMDAO vslDao = new CCDVslCodeMDAO();
			vslOwnCode = Formatter.nullTrim(vslDao.vesselOwnSelect(otcSaWithInfo.getVsl_code(), conn));

			CCDTrsactTypeMDAO tdao = new CCDTrsactTypeMDAO();

			if(vslOwnCode.equals("L")) {
				if( "T".equals(otcSaWithInfo.getCht_in_out_code())) {
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
			sb.append("	INSERT INTO OTC_SA_DETAIL 												\n");
			sb.append("		( 	SA_NO, SA_SEQ, VSL_CODE, VOY_NO, TRSACT_CODE, 					\n"); //[1]
			sb.append("			USD_SA_AMT, LOC_SA_AMT, KRW_SA_AMT,  							\n"); //[2]
			sb.append("			CURCY_CODE, EXC_DATE, EXC_RATE_TYPE, 							\n"); //[3]
			sb.append("			DUE_DATE, TERMS_DATE, PYMT_HOLD_FLAG, PYMT_METH,				\n"); //[4]
			sb.append("			SA_RATE, SA_RATE_DUR,											\n"); //[5]
			sb.append("			FACTOR, VAT_FLAG, TAX_RATE, 									\n"); //[6]
			sb.append("			USD_VAT_SA_AMT, LOC_VAT_SA_AMT, KRW_VAT_SA_AMT, 				\n"); //[7]
			sb.append("			BNK_QTY, BNK_PRC,												\n"); //[8]
			sb.append("			STL_VSL_CODE, STL_VOY_NO, STL_CNTR_ACC_CODE,					\n"); //[9]
			sb.append("			USD_EXC_RATE, LOC_EXC_RATE, USD_LOC_RATE, 						\n"); //[10]
			sb.append("			BANK_ACC_ID, BANK_ACC_DESC,  									\n"); //[11]
			sb.append("			SYS_CRE_DATE, SYS_CRE_USER_ID, SYS_UPD_DATE, SYS_UPD_USER_ID,   	\n"); //[12]
			sb.append("			GL_ACC_CODE, EVIDENCE_FLAG         ) 	\n"); //[13]
			sb.append(" VALUES											\n");
			sb.append("		( 	?, ?, ?, ?, ?, 							\n"); //[1]
			sb.append("			?, ?, ?, 								\n"); //[2]
			sb.append("			?, ?, ?, 								\n"); //[3]
			sb.append("			?, ?, ?, ?,								\n"); //[4]
			sb.append("			?, ?, 									\n"); //[5]
			sb.append("			?, ?, ?, 								\n"); //[6]
			sb.append("			?, ?, ?, 								\n"); //[7]
			sb.append("			?, ?,  									\n"); //[8]
			sb.append("			?, ?, ?, 								\n"); //[9]
			sb.append("			?, ?, ?, 								\n"); //[10]
			sb.append("			?, ?,	 								\n"); //[11]
			sb.append("			SYSDATE, ?, SYSDATE, ?, 				\n"); //[12]
			sb.append("			?, ? )	                    			\n"); //[13]

			ps = conn.prepareStatement(sb.toString());

			//trsact_code : M001, income tax 소득세 = 지급총액/(1-rate*.1.1)
			int i = 1;
			// [1]
			ps.setLong(i++, Formatter.nullLong(otcSaWithInfo.getWith_sa_no()));
			ps.setLong(i++, Formatter.nullLong((dao.saSeqMaxNoSelect(otcSaWithInfo.getWith_sa_no(), conn))));
			ps.setString(i++, Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
			ps.setLong(i++, Formatter.nullLong(otcSaWithInfo.getVoy_no()));
			ps.setString(i++, "M001");
			// [2]
			ps.setDouble(i++, Formatter.nullDouble(otcSaWithInfo.getIncome_tax_amt_usd()));
			ps.setDouble(i++, Formatter.nullDouble(otcSaWithInfo.getIncome_tax_amt_usd()));
			ps.setDouble(i++, Formatter.nullDouble(otcSaWithInfo.getIncome_tax_amt_krw()));
			// [3]
			ps.setString(i++, Formatter.nullTrim(otcSaWithInfo.getCurcy_code()));
			ps.setTimestamp(i++, otcSaWithInfo.getExc_date());
			ps.setString(i++, Formatter.nullTrim(otcSaWithInfo.getExc_rate_type()));
			// [4]
			ps.setTimestamp(i++, otcSaWithInfo.getDue_date());
			ps.setTimestamp(i++, otcSaWithInfo.getTerms_date());
			ps.setString(i++, Formatter.nullTrim(otcSaWithInfo.getPymt_hold_flag()));
			//ps.setString(i++, Formatter.nullTrim(otcSaWithInfo.getPymt_meth()));
			ps.setString(i++, "CLEARING");
			// [5]
			ps.setDouble(i++, Formatter.nullDouble(otcSaWithInfo.getIncome_tax_base_usd()));   // taxable income
			ps.setDouble(i++, Formatter.nullDouble(otcSaWithInfo.getIncome_tax_rate()));   	   // tax rate
			System.out.println("RYU 원천징수 확인 otcSaWithInfo.getIncome_tax_rate()="+otcSaWithInfo.getIncome_tax_rate());
			// [6]
			ps.setLong(i++, 0);
			ps.setString(i++, "N");
			ps.setDouble(i++, 0);
			// [7]
			ps.setDouble(i++, 0);
			ps.setDouble(i++, 0);
			ps.setDouble(i++, 0);
			// [8]
			ps.setDouble(i++, 0);
			ps.setDouble(i++, 0);
			// [9]
			ps.setString(i++, Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
			ps.setLong(i++, Formatter.nullLong(otcSaWithInfo.getVoy_no()));
			ps.setDouble(i++, Formatter.nullDouble(otcSaWithInfo.getCntr_acc_code()));
			// [10]
			ps.setDouble(i++, Formatter.nullDouble(otcSaWithInfo.getUsd_exc_rate()));
			ps.setDouble(i++, Formatter.nullDouble(otcSaWithInfo.getLoc_exc_rate()));
			ps.setDouble(i++, Formatter.nullDouble(otcSaWithInfo.getUsd_loc_rate()));
			// [11]
			ps.setLong(i++, Formatter.nullLong(otcSaWithInfo.getBank_acc_id()));
			ps.setString(i++, Formatter.nullTrim(otcSaWithInfo.getBank_acc_desc()));
			// [12]
			ps.setString(i++, userBean.getUser_id());
			ps.setString(i++, userBean.getUser_id());

			CCDTrsactTypeMVO tVO = tdao.ccdTrsactTypeMPaymentSelect("SOMO", cht_in_out_code, "M001", conn);
			if (tVO != null) {
				gl_acc_code = Formatter.nullTrim(tVO.getO_gl_acc_code());   //RYU 2010.09.29
			}
			// [13]
			ps.setString(i++, gl_acc_code);
			ps.setString(i++, evidence_flag);

			ps.executeUpdate();
			ps.close();

			// trsact_code : M002, inhabitants tax 주민세 = (지급총액/(1-rate*.1.1))*0.1
			ps = conn.prepareStatement(sb.toString());
			int r = 1;
			// [1]
			ps.setLong(r++, Formatter.nullLong(otcSaWithInfo.getWith_sa_no()));
			ps.setLong(r++, Formatter.nullLong((dao.saSeqMaxNoSelect(otcSaWithInfo.getWith_sa_no(), conn))));
			ps.setString(r++, Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
			ps.setLong(r++, Formatter.nullLong(otcSaWithInfo.getVoy_no()));
			ps.setString(r++, "M002");
			// [2]
			ps.setDouble(r++, Formatter.nullDouble(otcSaWithInfo.getInhabit_tax_amt_usd()));
			ps.setDouble(r++, Formatter.nullDouble(otcSaWithInfo.getInhabit_tax_amt_usd()));
			ps.setDouble(r++, Formatter.nullDouble(otcSaWithInfo.getInhabit_tax_amt_krw()));
			// [3]
			ps.setString(r++, Formatter.nullTrim(otcSaWithInfo.getCurcy_code()));
			ps.setTimestamp(r++, otcSaWithInfo.getExc_date());
			ps.setString(r++, Formatter.nullTrim(otcSaWithInfo.getExc_rate_type()));
			// [4]
			ps.setTimestamp(r++, otcSaWithInfo.getDue_date());
			ps.setTimestamp(r++, otcSaWithInfo.getTerms_date());
			ps.setString(r++, Formatter.nullTrim(otcSaWithInfo.getPymt_hold_flag()));
			//ps.setString(r++, Formatter.nullTrim(otcSaWithInfo.getPymt_meth()));
			ps.setString(r++, "CLEARING");
			// [5]
			ps.setDouble(r++, Formatter.nullDouble(otcSaWithInfo.getInhabit_tax_base_usd()));  // taxable inhabit
			ps.setDouble(r++, Formatter.nullDouble(otcSaWithInfo.getInhabit_tax_rate()));      // tax rate
			System.out.println("RYU 원천징수 확인 otcSaWithInfo.getInhabit_tax_rate()="+otcSaWithInfo.getInhabit_tax_rate());
			// [6]
			ps.setLong(r++, 0);
			ps.setString(r++, "N");
			ps.setDouble(r++, 0);
			// [7]
			ps.setDouble(r++, 0);
			ps.setDouble(r++, 0);
			ps.setDouble(r++, 0);
			// [8]
			ps.setDouble(r++, 0);
			ps.setDouble(r++, 0);
			// [9]
			ps.setString(r++, Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
			ps.setLong(r++, Formatter.nullLong(otcSaWithInfo.getVoy_no()));
			ps.setDouble(r++, Formatter.nullDouble(otcSaWithInfo.getCntr_acc_code()));
			// [10]
			ps.setDouble(r++, Formatter.nullDouble(otcSaWithInfo.getUsd_exc_rate()));
			ps.setDouble(r++, Formatter.nullDouble(otcSaWithInfo.getLoc_exc_rate()));
			ps.setDouble(r++, Formatter.nullDouble(otcSaWithInfo.getUsd_loc_rate()));
			// [11]
			ps.setLong(r++, Formatter.nullLong(otcSaWithInfo.getBank_acc_id()));
			ps.setString(r++, Formatter.nullTrim(otcSaWithInfo.getBank_acc_desc()));
			// [12]
			ps.setString(r++, userBean.getUser_id());
			ps.setString(r++, userBean.getUser_id());

			tVO = tdao.ccdTrsactTypeMPaymentSelect("SOMO", cht_in_out_code, "M002", conn);
			if (tVO != null) {
				gl_acc_code = Formatter.nullTrim(tVO.getO_gl_acc_code());   //RYU 2010.09.29
			}
			// [13]
			ps.setString(r++, gl_acc_code);
			ps.setString(r++, evidence_flag);

			ps.executeUpdate();
			ps.close();


//---------------------------- 형일 수정 (시작) --------------------------------------------
			log.debug("getWth_tax_calc_method  : " + otcSaWithInfo.getWth_tax_calc_method() );

			//------------------------------------------------------------------------
			// ## 원천징수 보완 ## ( hijang - 2012.04.19 )
			// Gross-Up 방식  :  M004 가 발생해야 한다. ( 원래 방식 그대로 )
			// Deduction 방식  : M004 가 발생되면 안된다..!!
			//------------------------------------------------------------------------
			if( otcSaWithInfo.getWth_tax_calc_method().equals("1"))	{	// Gross-Up 방식
				// trsact_code : M004, onhire(withholding tax) 지급운임기타 = 소득세+주민세[ 지급총액/(1-rate*.1.1) + (지급총액/(1-rate*.1.1))*0.1 ]
				ps = conn.prepareStatement(sb.toString());
				int s = 1;
				// [1]
				ps.setLong(s++, Formatter.nullLong(otcSaWithInfo.getWith_sa_no()));
				ps.setLong(s++, Formatter.nullLong((dao.saSeqMaxNoSelect(otcSaWithInfo.getWith_sa_no(), conn))));
				ps.setString(s++, Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
				ps.setLong(s++, Formatter.nullLong(otcSaWithInfo.getVoy_no()));
				// [2]
				ps.setString(s++, "M004");
				ps.setDouble(s++, Formatter.nullDouble(otcSaWithInfo.getIncome_tax_amt_usd())+Formatter.nullDouble(otcSaWithInfo.getInhabit_tax_amt_usd()));
				ps.setDouble(s++, Formatter.nullDouble(otcSaWithInfo.getIncome_tax_amt_usd())+Formatter.nullDouble(otcSaWithInfo.getInhabit_tax_amt_usd()));
				ps.setDouble(s++, Formatter.nullDouble(otcSaWithInfo.getIncome_tax_amt_krw())+Formatter.nullDouble(otcSaWithInfo.getInhabit_tax_amt_krw()));
				// [3]
				ps.setString(s++, Formatter.nullTrim(otcSaWithInfo.getCurcy_code()));
				ps.setTimestamp(s++, otcSaWithInfo.getExc_date());
				ps.setString(s++, Formatter.nullTrim(otcSaWithInfo.getExc_rate_type()));
				// [4]
				ps.setTimestamp(s++, otcSaWithInfo.getDue_date());
				ps.setTimestamp(s++, otcSaWithInfo.getTerms_date());
				ps.setString(s++, Formatter.nullTrim(otcSaWithInfo.getPymt_hold_flag()));
				//ps.setString(s++, Formatter.nullTrim(otcSaWithInfo.getPymt_meth()));
				ps.setString(s++, "CLEARING");
				// [5]
				ps.setDouble(s++, 0);
				ps.setDouble(s++, 0);
				// [6]
				ps.setLong(s++, 0);
				ps.setString(s++, "N");
				ps.setDouble(s++, 0);
				// [7]
				ps.setDouble(s++, 0);
				ps.setDouble(s++, 0);
				ps.setDouble(s++, 0);
				// [8]
				ps.setDouble(s++, 0);
				ps.setDouble(s++, 0);
				// [9]
				ps.setString(s++, Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
				ps.setLong(s++, Formatter.nullLong(otcSaWithInfo.getVoy_no()));
				ps.setDouble(s++, Formatter.nullDouble(otcSaWithInfo.getCntr_acc_code()));
				// [10]
				ps.setDouble(s++, Formatter.nullDouble(otcSaWithInfo.getUsd_exc_rate()));
				ps.setDouble(s++, Formatter.nullDouble(otcSaWithInfo.getLoc_exc_rate()));
				ps.setDouble(s++, Formatter.nullDouble(otcSaWithInfo.getUsd_loc_rate()));
				// [11]
				ps.setLong(s++, Formatter.nullLong(otcSaWithInfo.getBank_acc_id()));
				ps.setString(s++, Formatter.nullTrim(otcSaWithInfo.getBank_acc_desc()));
				// [12]
				ps.setString(s++, userBean.getUser_id());
				ps.setString(s++, userBean.getUser_id());

				tVO = tdao.ccdTrsactTypeMPaymentSelect("SOMO", cht_in_out_code, "M004", conn);
				if (tVO != null) {
					gl_acc_code = Formatter.nullTrim(tVO.getO_gl_acc_code());   //RYU 2010.09.29
				}
				// [13]
				ps.setString(s++, gl_acc_code);
				ps.setString(s++, evidence_flag);

				ps.executeUpdate();
				ps.close();

			}else{

				//----------------------------------------------------------------------
				// Deduction 방식은 'M004' 가 생성 안되는 관계로, balance 재계산처리(hijang)
				//----------------------------------------------------------------------

				String crTot = "	select sum(nvl(b.usd_sa_amt,0)) + sum(nvl(b.usd_vat_sa_amt,0)) as usd_amt, sum(nvl(b.krw_sa_amt,0))+ sum(nvl(b.krw_vat_sa_amt,0)) as krw_amt  ";
				crTot = crTot + "	from otc_sa_head a, otc_sa_detail b, ccd_trsact_type_m c  ";
				crTot = crTot + "	where a.sa_no = b.sa_no  ";
				crTot = crTot + "	and a.sa_no = ?     ";
				crTot = crTot + "	and b.trsact_code = c.trsact_code  ";
				crTot = crTot + "	and c.o_sa_rpt_debit_credit = '2'   ";
				crTot = crTot + "	and c.som_system_type ='SOMO'        ";
				crTot = crTot + "	and a.cht_in_out_code  = c.own_vsl_category  ";
				crTot = crTot + "	and b.trsact_code not in ( 'L001' , 'L002')   ";

				String drTot = "	select  sum(nvl(b.usd_sa_amt,0)) + sum(nvl(b.usd_vat_sa_amt,0)) as usd_amt, sum(nvl(b.krw_sa_amt,0))+ sum(nvl(b.krw_vat_sa_amt,0)) as krw_amt  ";
				drTot = drTot + "	from otc_sa_head a, otc_sa_detail b, ccd_trsact_type_m c  ";
				drTot = drTot + "	where a.sa_no = b.sa_no  ";
				drTot = drTot + "	and a.sa_no =?   ";
				drTot = drTot + "	and b.trsact_code = c.trsact_code  ";
				drTot = drTot + "	and c.o_sa_rpt_debit_credit = '1'  ";
				drTot = drTot + "	and c.som_system_type ='SOMO'  ";
				drTot = drTot + "	and a.cht_in_out_code  = c.own_vsl_category  ";
				drTot = drTot + "	and b.trsact_code not in ( 'L001' , 'L002')  ";

				double cr_loc_amt = 0;
				double cr_won_amt = 0;
				double dr_loc_amt = 0;
				double dr_won_amt = 0;
				double loc_amt = 0;
				double won_amt = 0;

				// debit balance 총합을 구함 ============================================
				ps = conn.prepareStatement(drTot);
				int k1 = 1;

				ps.setLong(k1++, Formatter.nullLong(otcSaWithInfo.getWith_sa_no()));

				rs = ps.executeQuery();

				while (rs.next()) {
					dr_loc_amt = Formatter.nullDouble(new Double(rs.getDouble("usd_amt")));
					dr_won_amt = Formatter.nullDouble(new Double(rs.getDouble("krw_amt")));
				}

				ps.close();
				rs.close();

				// credit 총합을 구함 ============================================
				ps = conn.prepareStatement(crTot);
				int k2 = 1;

				ps.setLong(k2++, Formatter.nullLong(otcSaWithInfo.getWith_sa_no()));

				rs = ps.executeQuery();

				while (rs.next()) {
					cr_loc_amt = Formatter.nullDouble(new Double(rs.getDouble("usd_amt")));
					cr_won_amt = Formatter.nullDouble(new Double(rs.getDouble("krw_amt")));
				}

				loc_amt = dr_loc_amt - cr_loc_amt;
				won_amt = dr_won_amt - cr_won_amt;

				ps.close();
				rs.close();


				StringBuffer sb2 = new StringBuffer();

				sb2.append("		UPDATE OTC_SA_DETAIL 			\n");
				sb2.append("		SET  USD_SA_AMT = ?,   LOC_SA_AMT = ?,    KRW_SA_AMT = ?   \n");
				sb2.append("		WHERE	 						\n");
				sb2.append("				sa_no  = ?  			\n");
				sb2.append("		AND	trsact_code  =  ?    ");

				ps = conn.prepareStatement(sb2.toString());

				int s = 1;
				ps.setDouble(s++, loc_amt);
				ps.setDouble(s++, loc_amt);
				ps.setDouble(s++, won_amt);
				ps.setLong(s++, Formatter.nullLong(otcSaWithInfo.getWith_sa_no()));


				if (loc_amt >= 0) {
					ps.setString(s++,"L001");
				} else {
					ps.setString(s++,"L002");
				}

				ps.executeUpdate();
				ps.close();
			}

//---------------------------- 형일 수정 (종료) --------------------------------------------


			// 초기화
			sb.setLength(0);
			// Query 가져오기

			sb.append("		UPDATE OTC_SA_HEAD SET  WTH_HIRE_BAL_AMT = ? \n");
			sb.append("		WHERE SA_NO  = ?  	\n");
			sb.append("		AND	VSL_CODE = ?    \n");
			sb.append("		AND	VOY_NO   = ?	\n");
			sb.append("		AND	STEP_NO  = ?    ");

			ps = conn.prepareStatement(sb.toString());

			int q = 1;
			ps.setDouble(q++, Formatter.nullDouble(otcSaWithInfo.getOnHire_balance()));
			ps.setLong(q++, Formatter.nullLong(otcSaWithInfo.getWith_sa_no()));
			ps.setString(q++, Formatter.nullTrim(otcSaWithInfo.getVsl_code()));
			ps.setLong(q++, Formatter.nullLong(otcSaWithInfo.getVoy_no()));
			ps.setLong(q++, Formatter.nullLong(otcSaWithInfo.getStep_no()));

			ps.executeUpdate();
			ps.close();

			//conn.commit();
			//conn.setAutoCommit(true);

			result = "SUC-0100";

		} catch (Exception e) {
			throw new STXException(e);
		}finally{
			if(ps != null) ps.close();
		}
		return result;
	}

	public String saWithTaxInvoiceDelete(Long saNo, String vslCode, Long voyNo, String chtInOutCode, Long stepNo, OTCSaHeadDTO otcSaHeadDto, UserBean userBean, Connection conn) throws Exception, STXException {
		String result = "";
		PreparedStatement ps = null;
		try {
			StringBuffer sb = new StringBuffer();
			// Query 가져오기
			sb.append("	DELETE FROM OTC_SA_DETAIL 	\n");
			sb.append("	WHERE SA_NO  = ?  			\n");
			sb.append("	AND	VSL_CODE = ?    		\n");
			sb.append("	AND	VOY_NO   = ?			\n");
			sb.append("	AND TRSACT_CODE IN ('M001', 'M002', 'M004') ");

			ps = conn.prepareStatement(sb.toString());

			int q = 1;
			ps.setLong(q++, saNo.longValue());
			ps.setString(q++, vslCode);
			ps.setLong(q++, voyNo.longValue());
			ps.executeUpdate();
			ps.close();

			// 초기화
			sb.setLength(0);
			// Query 가져오기

			sb.append("		UPDATE OTC_SA_HEAD SET  WTH_HIRE_BAL_AMT = ? \n");
			sb.append("		WHERE SA_NO  = ?  	\n");
			sb.append("		AND	VSL_CODE = ?    \n");
			sb.append("		AND	VOY_NO   = ?	\n");
			sb.append("		AND	STEP_NO  = ?    ");

			ps = conn.prepareStatement(sb.toString());

			int r = 1;
			ps.setDouble(r++, 0 );
			ps.setLong(r++, Formatter.nullLong(otcSaHeadDto.getSa_no()));
			ps.setString(r++, Formatter.nullTrim(otcSaHeadDto.getVsl_code()));
			ps.setLong(r++, Formatter.nullLong(otcSaHeadDto.getVoy_no()));
			ps.setLong(r++, Formatter.nullLong(otcSaHeadDto.getStep_no()));

			ps.executeUpdate();
			ps.close();

			result = "delete";

		} catch (Exception e) {
			throw new STXException(e);
		}finally{
			if(ps != null) ps.close();
		}
		return result;
	}

	public String saHeadWthHireBalReuceRateUpdate(OTCSaHeadVO saHeadVO, UserBean userBean, Connection conn) throws Exception, STXException {
		String result = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		long saCnt = 0;
		try {
			log.debug("sa_no:"+saHeadVO.getSa_no());
			StringBuffer sb = new StringBuffer();
			sb.append("		select count(a.sa_no) saCnt				\n");
			sb.append("		from otc_sa_head a, otc_sa_detail b		\n");
			sb.append("		where a.sa_no = b.sa_no					\n");
			sb.append("		and a.sa_no = ?     					\n");
			sb.append("		and a.wth_flag = 'Y'					\n");
			sb.append("		and a.cht_in_out_code = 'T' 			\n");
			sb.append("		and a.cancel_flag = 'N'					\n");
			//sb.append("		and b.trsact_code in ('A001','A002','G001','G002','H001','H002','H005','I003','I004','I005')	\n");
			//결산마감 로직변경( SOA 실계정 및 실채산항차 매핑 처리 ) - hijang 20141218
			sb.append("		and b.trsact_code in ('A001','A002','A006','A007', 'G001','G002','G003','G004', 'H001','H002','H009','H010', 'H005','H011', 'I003','I004','I005','I071','I072','I073')	\n");

			log.debug(" [saHeadWthHireBalReuceRateUpdate query]"+sb.toString());

			ps = conn.prepareStatement(sb.toString());
			int i = 1;
			ps.setLong(i++, saHeadVO.getSa_no().longValue());

			rs = ps.executeQuery();
			while (rs.next()) {
				saCnt = rs.getLong("saCnt");
			}
			ps.close();
			rs.close();

			// 초기화
			sb.setLength(0);
			if(saCnt > 0){
				// Query 가져오기
				sb.append("		UPDATE OTC_SA_HEAD SET WTH_HIRE_BAL_REDUCE_RATE = ? \n");
				sb.append("		WHERE SA_NO  = ?  			\n");
				sb.append("		AND	VSL_CODE = ?    		\n");
				sb.append("		AND	VOY_NO   = ?			\n");
				sb.append("		AND	STEP_NO  = ?   			\n");
				sb.append("		AND CHT_IN_OUT_CODE = ? 	");

				ps = conn.prepareStatement(sb.toString());

				int q = 1;
				ps.setLong(q++, 1);
				ps.setLong(q++, saHeadVO.getSa_no().longValue());
				ps.setString(q++, saHeadVO.getVsl_code());
				ps.setLong(q++, saHeadVO.getVoy_no().longValue());
				ps.setLong(q++, saHeadVO.getStep_no().longValue());
				ps.setString(q++, saHeadVO.getCht_in_out_code());

				ps.executeUpdate();
				ps.close();

				result = "Y";
			} else {
				// Query 가져오기
				sb.append("		UPDATE OTC_SA_HEAD SET WTH_HIRE_BAL_REDUCE_RATE = ? \n");
				sb.append("		WHERE SA_NO  = ?  			\n");
				sb.append("		AND	VSL_CODE = ?    		\n");
				sb.append("		AND	VOY_NO   = ?			\n");
				sb.append("		AND	STEP_NO  = ?   			\n");
				sb.append("		AND CHT_IN_OUT_CODE = ? 	");

				ps = conn.prepareStatement(sb.toString());

				int q = 1;
				ps.setLong(q++, 0);
				ps.setLong(q++, saHeadVO.getSa_no().longValue());
				ps.setString(q++, saHeadVO.getVsl_code());
				ps.setLong(q++, saHeadVO.getVoy_no().longValue());
				ps.setLong(q++, saHeadVO.getStep_no().longValue());
				ps.setString(q++, saHeadVO.getCht_in_out_code());

				ps.executeUpdate();
				ps.close();

				result = "N";
			}

		} catch (Exception e) {
			throw new STXException(e);
		}finally{
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}

		return result;
	}

	/**
	 * <p>
	 * 설명:sa Detail 중 pymt hold flag 를  조회하는 메소드이다.
	 * @author hermosa 111018
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public OTCSaDetailVO saDetailCheckPymtHoldFlag(Long saNo, Connection conn) throws STXException {

		OTCSaDetailVO result = null;

		try {
			DbWrap dbWrap = new DbWrap();

			StringBuffer sb = new StringBuffer();
			// Query 가져오기

			sb.append("		SELECT V.PYMT_HOLD_FLAG   ");
			sb.append("	          FROM OTC_SA_DETAIL V    ");
			sb.append(" WHERE SA_NO = " + saNo.longValue() + " ");
			sb.append(" AND PYMT_HOLD_FLAG = 'Y' ");
			sb.append(" AND TRSACT_CODE = 'L001' ");

			result = (OTCSaDetailVO) dbWrap.getObject(conn, OTCSaDetailVO.class, sb.toString());

		} catch (Exception e) {

			throw new STXException(e);
		}
		return result;
	}

	/*
	 * 법원허가번호 update 한다.
	 * AP가 존재할 경우에 AP LINE 에 저장한다.
	 */
	public String courtAdmitNoUpdate(OTCBalanceHeadDTO infos, UserBean userBean, Connection conn) throws Exception, STXException {
		String result = "";
		PreparedStatement ps = null;
		try {


			if (infos.getSa_no() != null) {

				StringBuffer sb = new StringBuffer();
				// Query 가져오기

				sb.append("		UPDATE OTC_SA_DETAIL SET  ");
				sb.append("			     COURT_FLAG = ? ,          ");
				sb.append("		       COURT_ADMIT_NO =  ? ,     ");
				sb.append("		   	   SYS_UPD_DATE = SYSDATE , ");
				sb.append("			   SYS_UPD_USER_ID = ?  ");
				sb.append("		 WHERE SA_NO = ?  ");
				sb.append("		 AND TRSACT_CODE IN  ('L001')");

				ps = conn.prepareStatement(sb.toString());

				int i = 1;

				ps.setString(i++, Formatter.nullTrim(infos.getCourt_flag()));
				ps.setString(i++, Formatter.nullTrim(infos.getCourt_admit_no()));

				ps.setString(i++, Formatter.nullTrim(userBean.getUser_id()));
				ps.setLong(i++, infos.getSa_no().longValue());

				ps.executeUpdate();

				result = "SUC-0600";

			}

		} catch (Exception e) {
			throw new STXException(e);
		}finally{
			if(ps != null) ps.close();
		}
		return result;
	}


	/*
	 * SOA 입력 ITEM 들의 FROM-TO 기간에 대해서, 해당 채산항차 START-END DATE 에 걸치지 않은건 있는지 체크한다.
	 * 2014.12.23  HIJANG
	 */
	public ArrayList SoaVsCBDurationCheck(String vslCode, Long VoyNo, String chtInOut, Long stepNo, UserBean userBean, Connection conn) throws Exception, STXException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList array = null;
		String invalid_item = "";

		try {
			StringBuffer sb = new StringBuffer();

			/*---------------------------------------------------------------------------------------------------------------------------------------
			sb.append("\n   SELECT M.DIVIDED_GROUP, M.TRSACT_CODE,                            				");
			sb.append("\n   	   TRSACT_NAME_FUNC('SOMO', 'T', M.TRSACT_CODE ) TRSACT_NAME,  				");
			sb.append("\n   	   M.FAIL_ITEM, M.PASS_ITEM                                   				");
			sb.append("\n   FROM                                                                                                   ");
			sb.append("\n   (                                                                                                      ");
			sb.append("\n     SELECT T.DIVIDED_GROUP, T.TRSACT_CODE, MAX(T.FAIL_ITEM) FAIL_ITEM, MAX(T.PASS_ITEM) PASS_ITEM        ");
			sb.append("\n     FROM                                                                                                 ");
			sb.append("\n     (                                                                                                    ");
			sb.append("\n       SELECT K.DIVIDED_GROUP, K.TRSACT_CODE, DECODE(K.IS_VALID,'FAIL','FAIL','') AS FAIL_ITEM, DECODE(K.IS_VALID,'PASS','PASS','') AS PASS_ITEM  ");
			sb.append("\n       FROM                                                              ");
			sb.append("\n       (                                                                 ");
			sb.append("\n         SELECT S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID               ");
			sb.append("\n         FROM                                                            ");
			sb.append("\n         (                                                               ");
			sb.append("\n             SELECT SOA.*,                                               ");
			sb.append("\n                    CB.*,                                                ");
			sb.append("\n                      CASE WHEN ( ( SOA.RECALC_FROM_DATE >= CB.VOY_STRT_DATE  and  SOA.RECALC_FROM_DATE <=  CB.VOY_END_DATE )       ");
			sb.append("\n                                 or ( SOA.RECALC_TO_DATE >= CB.VOY_STRT_DATE  and  SOA.RECALC_TO_DATE <=  CB.VOY_END_DATE )         ");
			sb.append("\n                                 or ( CB.VOY_STRT_DATE >= SOA.RECALC_FROM_DATE  and  CB.VOY_STRT_DATE <=  SOA.RECALC_TO_DATE )      ");
			sb.append("\n                                 or ( CB.VOY_END_DATE >= SOA.RECALC_FROM_DATE  and  CB.VOY_END_DATE <=  SOA.RECALC_TO_DATE )        ");
			sb.append("\n                                ) THEN 'PASS'                                                                                       ");
			sb.append("\n                           WHEN   ( SOA.RECALC_FROM_DATE IS NULL OR SOA.RECALC_TO_DATE IS NULL ) THEN 'PASS'   ");		// SOA 기간 없는건 -> 무조건 PASS 시킨다
			sb.append("\n                           ELSE 'FAIL'                                   						 ");
			sb.append("\n                           END IS_VALID                                  						 ");
			sb.append("\n             FROM                                                        						 ");
			sb.append("\n             (                                                           						 ");
			sb.append("\n                   SELECT    H.SA_NO, H.VSL_CODE, H.VOY_NO, DECODE(H.CHT_IN_OUT_CODE,'T','T', 'C','T', 'O') AS CHT_IN_OUT_CODE,     ");
			sb.append("\n                              H.STEP_NO, H.CNTR_NO,                                             ");
			sb.append("\n                              D.TRSACT_CODE,                                                    ");
			sb.append("\n                              D.SA_RATE_DUR, D.LOC_SA_AMT,  D.USD_SA_AMT, D.KRW_SA_AMT,         ");
			sb.append("\n                              D.FROM_DATE,                                                      ");
			sb.append("\n                              D.TO_DATE,                                                        ");
			sb.append("\n                              CASE WHEN( D.FROM_DATE <= D.TO_DATE ) THEN                        ");
			sb.append("\n                                                D.FROM_DATE                                     ");
			sb.append("\n                                            ELSE                                                ");
			sb.append("\n                                                D.TO_DATE                                       ");
			sb.append("\n                                            END RECALC_FROM_DATE,                               ");
			sb.append("\n                                     CASE WHEN( D.FROM_DATE <= D.TO_DATE ) THEN                 ");
			sb.append("\n                                                D.TO_DATE                                       ");
			sb.append("\n                                            ELSE                                                ");
			sb.append("\n                                                D.FROM_DATE                                     ");
			sb.append("\n                                            END RECALC_TO_DATE,                                 ");
			sb.append("\n                              ROWNUM  AS DIVIDED_GROUP         								 "); 	// 배분된 항차를 찾는 Group Key
			sb.append("\n                   FROM OTC_SA_HEAD H, OTC_SA_DETAIL D                                          ");
			sb.append("\n                   WHERE                                                                        ");
			sb.append("\n                       H.SA_NO = D.SA_NO                                                        ");
			sb.append("\n                   AND H.STEP_NO <> 0                                                           ");
			sb.append("\n                   AND D.TRSACT_CODE IN  ('A006', 'A007', 'A008', 'A009', 'H009', 'H010', 'I071', 'I072', 'I073')     ");
			sb.append("\n                   AND D.USD_SA_AMT <> 0                           ");
			// 테스트 데이터
			sb.append("\n	        		AND H.VSL_CODE = ?                              ");
			sb.append("\n	        		AND H.VOY_NO = ?                                ");
			sb.append("\n	        		AND H.CHT_IN_OUT_CODE = ?                       ");
			sb.append("\n	        		AND H.STEP_NO = ?                               ");
			sb.append("\n             ) SOA,                                                ");
			//sb.append("\n             CB_ALL_VOY_INFO_V CB                                ");
			sb.append("\n             CB_ALL_VOY_INFO_ADJUST_V CB                           ");	 // 기간 중첩되는 채산항차 처리 (2015.01.03)
			sb.append("\n             WHERE                                                 ");
			sb.append("\n                   SOA.VSL_CODE = CB.VSL_CODE                      ");
			sb.append("\n             AND SOA.CHT_IN_OUT_CODE = CB.VOYAGE_FLAG              ");
			sb.append("\n             AND SOA.CNTR_NO = CB.CNTR_NO               			");	 // 계약번호까지 JOIN 걸어야 됨. (hijang 2015.01.15)
			sb.append("\n         ) S                                                       ");
			sb.append("\n         GROUP BY S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID       ");
			sb.append("\n         ORDER BY S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID       ");
			sb.append("\n       ) K                                                         ");
			sb.append("\n     ) T                                                           ");
			sb.append("\n     GROUP BY T.DIVIDED_GROUP, T.TRSACT_CODE                       ");
			sb.append("\n     ORDER BY T.DIVIDED_GROUP, T.TRSACT_CODE                       ");
			sb.append("\n   ) M                                                             ");
			sb.append("\n   WHERE                                                           ");
			sb.append("\n   M.FAIL_ITEM = 'FAIL' AND M.PASS_ITEM IS NULL                 	");	// 기간 오류 ITEM 만 추출 (FAIL 건만 존재하는건 추출)
			sb.append("\n   ORDER BY M.DIVIDED_GROUP, M.TRSACT_CODE                         ");
			---------------------------------------------------------------------------------------------------------------------------------------*/


			// GMT 와 LOCAL 시간 - DIFF 차이 처리용 ( hijang 2015.02.09 )
			sb.append("\n   SELECT M.DIVIDED_GROUP, M.TRSACT_CODE,                                    												");
			sb.append("\n          TRSACT_NAME_FUNC('SOMO', 'T', M.TRSACT_CODE ) TRSACT_NAME,                                                       ");
			sb.append("\n          M.FAIL_ITEM, M.PASS_ITEM                                                                                         ");
			sb.append("\n   FROM                                                                                                                    ");
			sb.append("\n   (                                                                                                                       ");
			sb.append("\n     SELECT T.DIVIDED_GROUP, T.TRSACT_CODE, MAX(T.FAIL_ITEM) FAIL_ITEM, MAX(T.PASS_ITEM) PASS_ITEM                         ");
			sb.append("\n     FROM                                                                                                                  ");
			sb.append("\n     (                                                                                                                     ");
			sb.append("\n       SELECT K.DIVIDED_GROUP, K.TRSACT_CODE, DECODE(K.IS_VALID,'FAIL','FAIL','') AS FAIL_ITEM, DECODE(K.IS_VALID,'PASS','PASS','') AS PASS_ITEM  ");
			sb.append("\n       FROM                                                                                                                 ");
			sb.append("\n       (                                                                                                                    ");
			sb.append("\n         SELECT S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID                                                                  ");
			sb.append("\n         FROM                                                                                                               ");
			sb.append("\n         (                                                                                                                  ");
			sb.append("\n              SELECT SS.DIVIDED_GROUP, SS.TRSACT_CODE,                                                                      ");
			sb.append("\n                     SS.RECALC_FROM_DATE, SS.RECALC_TO_DATE,                                                                ");
			sb.append("\n                     SS.VOY_STRT_DATE, SS.VOY_END_DATE,                                                                     ");
			sb.append("\n                     -- VALID 여부 체크..                                                                                   	 ");
			sb.append("\n                     CASE WHEN ( ( SS.RECALC_FROM_DATE >= SS.VOY_STRT_DATE  and  SS.RECALC_FROM_DATE <=  SS.VOY_END_DATE )  ");
			sb.append("\n                               or ( SS.RECALC_TO_DATE >= SS.VOY_STRT_DATE  and  SS.RECALC_TO_DATE <=  SS.VOY_END_DATE )     ");
			sb.append("\n                               or ( SS.VOY_STRT_DATE >= SS.RECALC_FROM_DATE  and  SS.VOY_STRT_DATE <=  SS.RECALC_TO_DATE )  ");
			sb.append("\n                               or ( SS.VOY_END_DATE >= SS.RECALC_FROM_DATE  and  SS.VOY_END_DATE <=  SS.RECALC_TO_DATE )    ");
			sb.append("\n                              ) THEN 'PASS'                                                                                 ");
			sb.append("\n                         WHEN   ( SS.RECALC_FROM_DATE IS NULL OR SS.RECALC_TO_DATE IS NULL ) THEN 'PASS'       			 ");	// SOA 기간 없는건 -> 무조건 PASS 시킨다
			sb.append("\n                         ELSE 'FAIL'                                                          ");
			sb.append("\n                         END IS_VALID                                                         ");
			sb.append("\n              FROM                                                                            ");
			sb.append("\n              (                                                                               ");
			sb.append("\n                  SELECT ZZ.DIVIDED_GROUP, ZZ.TRSACT_CODE,                                    ");
			sb.append("\n                         ZZ.RECALC_FROM_DATE, ZZ.RECALC_TO_DATE,                              ");
			sb.append("\n                         --ZZ.VOY_STRT_DATE, ZZ.VOY_END_DATE,                                 ");
			//----------------------------------------------------------------------------------------------------
			// SOA와 채산상의 시간 DIFF(GMT 와 LOCAL 시간) 로 인하여,
			// 기존 채산 VOY_STRT_DATE 와 VOY_END_DATE 를 +/- 12H 만큼씩 보정해줌 !! ( HIJANG 2015.02.09 )
			//---------------------------------------------------------------------------------------------------
			sb.append("\n                          CASE WHEN ( ZZ.SOA_TO_CB_STRT_DIFF < 0 AND ZZ.SOA_TO_CB_STRT_DIFF >= -0.5 ) THEN		");
			sb.append("\n                                    DECODE( CNT, 0, ZZ.VOY_STRT_DATE - 0.5, ZZ.VOY_STRT_DATE )	");  // (-) 12시간 보정
			sb.append("\n                               ELSE                                                          	");
			sb.append("\n                                    ZZ.VOY_STRT_DATE                                         	");
			sb.append("\n                               END VOY_STRT_DATE,                                            	");
			sb.append("\n                          CASE WHEN ( ZZ.SOA_FROM_CB_END_DIFF > 0 AND ZZ.SOA_FROM_CB_END_DIFF <= 0.5 ) THEN       		");
			sb.append("\n                                    DECODE( CNT, 0, ZZ.VOY_END_DATE + 0.5, ZZ.VOY_END_DATE )	");  // (+) 12시간 보정
			sb.append("\n                               ELSE                                                          	");
			sb.append("\n                                    ZZ.VOY_END_DATE                                          	");
			sb.append("\n                               END VOY_END_DATE                                              	");
			sb.append("\n                  FROM                                                                       	");
			sb.append("\n                  (                                                                          	");
			sb.append("\n                      SELECT Z.DIVIDED_GROUP, Z.TRSACT_CODE,                                 	");
			sb.append("\n                             Z.RECALC_FROM_DATE, Z.RECALC_TO_DATE,                           	");
			sb.append("\n                             Z.VOY_STRT_DATE, Z.VOY_END_DATE,                                	");
			//SOA 기간(FROM-TO) 이 채산항차 기간( VOY_STRT_DATE,VOY_END_DATE) 에 포함된 갯수 체크용 ( hijang 2015.02.10 )
			sb.append("\n                             DECODE( Z.RECALC_FROM_DATE, NULL, 0, SOA_CB_DURATION_INCLUDE_CNT(Z.CNTR_NO, Z.RECALC_FROM_DATE, Z.RECALC_TO_DATE) ) AS CNT,  	");
			sb.append("\n                             ( Z.RECALC_TO_DATE - Z.VOY_STRT_DATE ) as SOA_TO_CB_STRT_DIFF,   	"); // '-0.5DAY ~ 0' 이내 존재시,, --> -12h 보정 필요
			sb.append("\n                             ( Z.RECALC_FROM_DATE - Z.VOY_END_DATE ) as SOA_FROM_CB_END_DIFF  	"); // '0 ~ +0.5DAY' 이내 존재시,, --> +12h 보정 필요
			sb.append("\n                      FROM                                                                     ");
			sb.append("\n                      (                                                                        ");
			sb.append("\n                           SELECT SOA.CNTR_NO, SOA.TRSACT_CODE,                                ");
			sb.append("\n                                  to_date(to_char(SOA.RECALC_FROM_DATE,'yyyymmddhh24mi'),'yyyymmddhh24miss') RECALC_FROM_DATE,        ");
			sb.append("\n                                  to_date(to_char(SOA.RECALC_TO_DATE,'yyyymmddhh24mi'), 'yyyymmddhh24miss') RECALC_TO_DATE,        ");
			sb.append("\n                                  SOA.DIVIDED_GROUP,      										");
			sb.append("\n                                  to_date(to_char(CB.VOY_STRT_DATE,'yyyymmddhh24mi'),'yyyymmddhh24miss') as VOY_STRT_DATE,       ");
			sb.append("\n                                  to_date(to_char(CB.VOY_END_DATE,'yyyymmddhh24mi'),'yyyymmddhh24miss') as VOY_END_DATE,       ");
			sb.append("\n                                  CB.VOYAGE_FLAG      											");
			sb.append("\n                           FROM                                                                ");
			sb.append("\n                           (                                                                   ");
			sb.append("\n                                 SELECT    H.SA_NO, H.VSL_CODE, H.VOY_NO, DECODE(H.CHT_IN_OUT_CODE,'T','T', 'C','T', 'O') AS CHT_IN_OUT_CODE,   ");
			sb.append("\n                                            H.STEP_NO, H.CNTR_NO,                                        ");
			sb.append("\n                                            D.TRSACT_CODE,                                               ");
			sb.append("\n                                            D.SA_RATE_DUR, D.LOC_SA_AMT,  D.USD_SA_AMT, D.KRW_SA_AMT,    ");
			sb.append("\n                                            D.FROM_DATE,                                                 ");
			sb.append("\n                                            D.TO_DATE,                                                   ");
			sb.append("\n                                            CASE WHEN( D.FROM_DATE <= D.TO_DATE ) THEN                   ");
			sb.append("\n                                                              D.FROM_DATE                                ");
			sb.append("\n                                                          ELSE                                           ");
			sb.append("\n                                                              D.TO_DATE                                  ");
			sb.append("\n                                                          END RECALC_FROM_DATE,                          ");
			sb.append("\n                                                   CASE WHEN( D.FROM_DATE <= D.TO_DATE ) THEN            ");
			sb.append("\n                                                              D.TO_DATE                                  ");
			sb.append("\n                                                          ELSE                                           ");
			sb.append("\n                                                              D.FROM_DATE                                ");
			sb.append("\n                                                          END RECALC_TO_DATE,                            ");
			sb.append("\n                                            ROWNUM  AS DIVIDED_GROUP                             		  ");	// 배분된 항차를 찾는 Group Key
			sb.append("\n                                 FROM OTC_SA_HEAD H, OTC_SA_DETAIL D                                     ");
			sb.append("\n                                 WHERE                                                                   ");
			sb.append("\n                                     H.SA_NO = D.SA_NO                                                   ");
			sb.append("\n                                 AND H.STEP_NO <> 0                                                      ");
			sb.append("\n                                 AND D.TRSACT_CODE IN  ( 'A006', 'A007', 'A008', 'A009',                 ");
			sb.append("\n                                             'H009', 'H010', 'I071', 'I072', 'I073', 'A003' )             ");	//A003 추가 (cve배분 170328 GYJ)
			sb.append("\n                                 AND D.USD_SA_AMT <> 0                           						  ");
			sb.append("\n                                 -- 테스트 데이터                                											  ");
			sb.append("\n                                 AND H.VSL_CODE = ?                        							");
			sb.append("\n                                 AND H.VOY_NO = ?                               						");
			sb.append("\n                                 AND H.CHT_IN_OUT_CODE = ?                     						");
			sb.append("\n                                 AND H.STEP_NO = ?                               						");
			sb.append("\n                                                                                 						");
			sb.append("\n                           ) SOA,                                                						");
			sb.append("\n                           CB_ALL_VOY_INFO_ADJUST_V CB                           						");  // 기간 중첩되는 채산항차 처리 (2015.01.03)
			sb.append("\n                           WHERE                                                 						");
			sb.append("\n                                 SOA.VSL_CODE = CB.VSL_CODE                      						");
			sb.append("\n                           AND SOA.CHT_IN_OUT_CODE = CB.VOYAGE_FLAG              						");
			sb.append("\n                           AND SOA.CNTR_NO = CB.CNTR_NO                          						");
			sb.append("\n                      ) Z                                                        						");
			sb.append("\n                 ) ZZ                                                            						");
			sb.append("\n             ) SS                                                                						");
			sb.append("\n         ) S                                                       			");
			sb.append("\n         GROUP BY S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID         			");
			sb.append("\n         ORDER BY S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID         			");
			sb.append("\n       ) K                                                           			");
			sb.append("\n     ) T                                                             			");
			sb.append("\n     GROUP BY T.DIVIDED_GROUP, T.TRSACT_CODE                         			");
			sb.append("\n     ORDER BY T.DIVIDED_GROUP, T.TRSACT_CODE                         			");
			sb.append("\n   ) M                                                               			");
			sb.append("\n   WHERE                                                             			");
			sb.append("\n   M.FAIL_ITEM = 'FAIL' AND M.PASS_ITEM IS NULL                     			"); 	// 기간 오류 ITEM 만 추출 (FAIL 건만 존재하는건 추출)
			sb.append("\n   ORDER BY M.DIVIDED_GROUP, M.TRSACT_CODE                         			");



			log.debug(" SoaVsCBDurationCheck : \n"+sb.toString());

			ps = conn.prepareStatement(sb.toString());

			int i = 1;
			ps.setString(i++, vslCode);
			ps.setLong(i++, VoyNo.longValue());
			ps.setString(i++, chtInOut);
			ps.setLong(i++, stepNo.longValue());

			rs = ps.executeQuery();

			array = new ArrayList();

			while (rs.next()) {
				invalid_item = rs.getString("TRSACT_NAME");
				//log.debug("invalid_item : "+ invalid_item) ;

				array.add(invalid_item);
			}

		} catch (Exception e) {
			throw new STXException(e);
		}finally{
			if(ps != null) ps.close();
		}
		return array;
	}

	/**
	 * <p>
	 * 설명:sa Detail 내역을 조회하는 메소드이다.
	 *     SOA, CB의 항차를 비교하여 모항차가 일치하지 않는 경우 배분하여 I/F WRITE한다.
	 * @param saNo :
	 *            sa 번호
	 * @return msgCode String: SA Detail 테이블에 발생하는 메소드를 리턴한다.
	 * @exception STXException :
	 *                saDetailSelect 실행하다 발생하는 모든 Exception을 처리한다
	 */
	public Collection SoaCBDiffAmtUpdateProdedureCall(Long saNo, String vslCode, Long VoyNo, String chtInOut, Long stepNo,UserBean userBean, Connection conn) throws STXException {

		Collection result = null;


		try {
			DbWrap dbWrap = new DbWrap();
			DbWrap dbWrap2 = new DbWrap();			//table update 후 data 조회.

			StringBuffer sb = new StringBuffer();

			ArrayList inVariable = new ArrayList();


			inVariable.add(String.valueOf(vslCode));
			inVariable.add(VoyNo);
			inVariable.add(String.valueOf(chtInOut));
			inVariable.add(stepNo);
			inVariable.add(String.valueOf(saNo));
			inVariable.add(userBean.getUser_id());

			log.debug(">>>>>>>P_SET_SA_CB_DIFF_AMT_UPDATE :---start");
			log.debug(">>>>>>>inVar.size()    : "+inVariable.size());

			Object basicDatas[] = dbWrap.getObjectCstmt(conn,"{ call P_SET_SA_CB_DIFF_AMT_UPDATE(?,?,?,?,?,?,?,?)  }",inVariable.toArray(),2);

			//Object objs[]       = dbwrap.getObjectCstmt(conn,sb.toString(),inVariable.toArray(),1);

			log.debug(">>>>>>>P_SET_SA_CB_DIFF_AMT_UPDATE :---end");

			int row = 0;
			String msg = (String) basicDatas[row++];
			String msgcode = (String) basicDatas[row++];

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

				result = dbWrap2.getObjects(conn, OTCSaCbDetailDTO.class, sb.toString());
			} else {
				log.error(">>>>>>P_SET_SA_CB_DIFF_AMT_UPDATE 작업 시 오류 발생했습니다. ");
				// Error 메시지
				throw new STXException("[System Error] P_SET_SA_CB_DIFF_AMT_UPDATE 작업 도중 오류가 발생했습니다.\n시스템 담당자에게 문의 바랍니다.");
			}


		} catch (Exception e) {

			throw new STXException(e);
		}
		return result;
	}


	/*
	 * CB에 의해 배분된 금액과 순수 SOA금액을 최종적으로 확인한다. 100% 일치해야함.
	 */
	public String SoaVsCBAmtCheck(String saNo, Connection conn) throws Exception, STXException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = null;
		double locDiffAmt = 0;
		double usdDiffAmt = 0;
		long krwDiffAmt = 0;
		double saDurRate = 0;

		try {
			StringBuffer sb = new StringBuffer();

			sb.append("		-- 조정 대상 데이터만 추출															\n");
			sb.append("		  SELECT  A.DEVIDED_GROUP,       -- 배분된 GROUP KEY								\n");
			sb.append("				  MAX(A.VOY_NO) VOY_NO,  -- LAST 항차 의미 (조정할 대상 항차)				\n");
			sb.append("				  MAX(A.TRSACT_CODE) TRSACT_CODE,											\n");
			sb.append("				  -- SOA 원금																\n");
			sb.append("				  MAX(A.ORI_SA_RATE_DUR) ORI_SA_RATE_DUR,									\n");
			sb.append("				  MAX(A.ORI_USD_SA_AMT) ORI_USD_SA_AMT,										\n");
			sb.append("				  MAX(A.ORI_LOC_SA_AMT) ORI_LOC_SA_AMT,										\n");
			sb.append("				  MAX(A.ORI_KRW_SA_AMT) ORI_KRW_SA_AMT,										\n");
			sb.append("				  -- CB 로 배분된 값														\n");
			sb.append("				  SUM(A.SA_RATE_DUR) DIVIDED_SA_RATE_DUR,									\n");
			sb.append("				  SUM(A.USD_SA_AMT) DIVIDED_USD_SA_AMT,										\n");
			sb.append("				  SUM(A.LOC_SA_AMT) DIVIDED_LOC_SA_AMT,										\n");
			sb.append("				  SUM(A.KRW_SA_AMT) DIVIDED_KRW_SA_AMT ,									\n");
			sb.append("				  -- DIFF 값																\n");
			sb.append("				  MAX(A.ORI_SA_RATE_DUR) -  SUM(A.SA_RATE_DUR) as  DIFF_RATE_DUR,			\n");
			sb.append("				  MAX(A.ORI_USD_SA_AMT) -  SUM(A.USD_SA_AMT) as  USD_DIFF_AMT,				\n");
			sb.append("				  MAX(A.ORI_LOC_SA_AMT) -  SUM(A.LOC_SA_AMT) as  LOC_DIFF_AMT,				\n");
			sb.append("				  MAX(A.ORI_KRW_SA_AMT) -  SUM(A.KRW_SA_AMT) as  KRW_DIFF_AMT				\n");
			sb.append("		  FROM OTC_SA_CB_DETAIL A															\n");
			sb.append("		  WHERE																				\n");
			sb.append("		  SA_NO = ?																	\n");
			//sb.append("		  AND A.TRSACT_CODE IN ( 'A006', 'A007', 'A008', 'A009', 'H009', 'H010'				\n");
			sb.append("		  AND A.TRSACT_CODE IN ( 'A006', 'A007', 'A008', 'A009', 'H009', 'H010'	,'A003'			\n");		//CVE 추가 170323 GYJ
			sb.append("							   ---------------------------------------------------------	\n");
			sb.append("							   -- SPEED CLAIM 은 실채산 VOY_NO 를 직접 입력하므로,,			\n");
			sb.append("							   -- 채산항차로 배분하지 않는다 ( hijang 2015.01.05 )			\n");
			sb.append("							   ---------------------------------------------------------	\n");
			sb.append("							   --, 'I071', 'I072', 'I073'									\n");
			sb.append("								 )															\n");
			sb.append("		  GROUP BY A.DEVIDED_GROUP															\n");


			log.debug(" SoaVsCBAmtCheck : \n"+sb.toString());

			ps = conn.prepareStatement(sb.toString());

			int i = 1;
			ps.setString(i++, saNo);

			rs = ps.executeQuery();

			while (rs.next()) {
				saDurRate = rs.getDouble("DIFF_RATE_DUR");
				locDiffAmt = rs.getDouble("LOC_DIFF_AMT");
				usdDiffAmt = rs.getDouble("USD_DIFF_AMT");
				krwDiffAmt = rs.getLong("KRW_DIFF_AMT");

				log.debug("saDurRate : "+ saDurRate) ;
				log.debug("locDiffAmt : "+ locDiffAmt) ;
				log.debug("usdDiffAmt : "+ usdDiffAmt) ;
				log.debug("krwDiffAmt : "+ krwDiffAmt) ;

				if(saDurRate != 0 || locDiffAmt != 0 || usdDiffAmt != 0 || krwDiffAmt != 0){
					if (saDurRate != 0){
						result = "DUR";
					}
					if (locDiffAmt != 0){
						result ="LOC AMT";
					}

					if (usdDiffAmt != 0){
						result ="USD AMT";
					}

					if (krwDiffAmt != 0){
						result ="KRW AMT";
					}
				}else{
					result = "";
				}
			}

		} catch (Exception e) {
			throw new STXException(e);
		}finally{
			if(ps != null) ps.close();
		}
		return result;
	}

	/*
	 * CB와 SOA의 Contract과 vsl이 동일한지 확인.
	 */
	public String SoaVsCBContractVslCheck(String vslCode,String contractNo, Connection conn) throws Exception, STXException {


		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = null;

		int cnt = 0;

		try {
			StringBuffer sb = new StringBuffer();

			sb.append("SELECT COUNT(*) CNT					\n");
			sb.append("  FROM CB_ALL_VOY_INFO_ADJUST_V A   	\n");
			sb.append(" WHERE A.VSL_CODE = ?     			\n");
			sb.append("   AND A.CNTR_NO = ?   				\n");

			log.debug(" vslCode : "+vslCode);
			log.debug(" contractNo : "+contractNo);
			log.debug(" SoaVsCBContractCheck : \n"+sb.toString());

			ps = conn.prepareStatement(sb.toString());

			int i = 1;
			ps.setString(i++, vslCode);
			ps.setString(i++, contractNo);

			rs = ps.executeQuery();

			while (rs.next()) {
				cnt = rs.getInt("CNT");

				log.debug("cnt : "+ cnt) ;

				if(cnt == 0){
					result = " 채산과 SOA의 계약번호 또는 Vessel Code가 상이 합니다.\n 확인바랍니다.\n";

				}else{
					result = "";
				}

			}



		} catch (Exception e) {
			throw new STXException(e);
		}finally{
			if(ps != null) ps.close();
		}
		return result;
	}


	/*
	 * CB에 의해 배분된 금액과 순수 SOA금액을 최종적으로 확인한다. 100% 일치해야함.
	 */
	public void soaCbDetailDivide(String start_date, String end_date, UserBean userBean, Connection conn) throws Exception, STXException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = null;

		try {

			StatementAccount sa = new StatementAccount();

			StringBuffer sb = new StringBuffer();

			sb.append("	  SELECT  DISTINCT A.VSL_CODE,      									\n");
			sb.append("				  A.VOY_NO,														\n");
			sb.append("				  A.CHT_IN_OUT_CODE,											\n");
			sb.append("				  A.STEP_NO,													\n");
			sb.append("				  A.CNTR_NO,													\n");
			sb.append("				  A.SA_NO														\n");
			sb.append("	  FROM OTC_SA_HEAD A, OTC_SA_DETAIL B									\n");	// DETAIL 데이터 없는건 제거 위해서.. JOIN 함.(HIJANG)
			sb.append("	  WHERE																	\n");
			sb.append("	  A.SA_NO = B.SA_NO														\n");
			sb.append("	  AND nvl(a.cancel_flag,'N') <> 'Y'										\n");
			//sb.append("		AND nvl(A.PROCESS_STS_FLAG,'N') = 'Y'								\n");
			sb.append("	  AND A.STEP_NO <> 0										\n");
			sb.append("	  AND TO_CHAR(a.posting_date, 'YYYYMMDD') BETWEEN ? AND ?				\n");
			sb.append("	  AND A.SA_NO not in ( SELECT m.SA_NO FROM OTC_SA_EXCEPT_LIST_TMP m  WHERE NVL(ATTRIBUTE5,'N') = 'Y' 	)	\n");	// 예외 SA_NO 관리용


			log.debug(" soaCbDetailDivide : \n"+sb.toString());

			log.debug(" start_date : " + start_date);
			log.debug(" end_date : " + end_date);

			int i = 1;
			ps = conn.prepareStatement(sb.toString());
			ps.setString(i++, start_date);
			ps.setString(i++, end_date);

			rs = ps.executeQuery();

			String vslCode = "";
			Long voyNo = new Long(0);
			String chtInCd = "";
			Long stepNo = new Long(0);
			String cntrNo = "";
			Long saNo = new Long(0);
			String ifType = "I" ;
			Timestamp cancelPostDate = null;
			String cancelReason = "";

			while (rs.next()) {

				vslCode = rs.getString("VSL_CODE");
				voyNo = new Long(rs.getLong("VOY_NO"));
				chtInCd = rs.getString("CHT_IN_OUT_CODE");
				stepNo = new Long(rs.getString("STEP_NO"));
				cntrNo = rs.getString("CNTR_NO");
				saNo = new Long(rs.getLong("SA_NO"));

log.debug("-------------------------------------") ;
log.debug("vsl_Code : " + vslCode) ;
log.debug("voy_No : " + voyNo) ;
log.debug("cht+In_Cd : " + chtInCd) ;
log.debug("step_No : " + stepNo) ;
log.debug("cntr_No : " + cntrNo) ;
log.debug("SA_NO : " + saNo) ;
log.debug("-------------------------------------") ;

				// 2) 항차매핑 및 배분 작업
				sa.soaCbDetailDivideCreate(vslCode, voyNo, chtInCd, stepNo, cntrNo, saNo, ifType, cancelPostDate, cancelReason, userBean, conn);

			}

		} catch (Exception e) {
			throw new STXException(e);
		}finally{
			if(ps != null) ps.close();
		}

		//return result;
	}


	/**
	 * <p>
	 * 설명: 로컬에서 SOA 채산항차매핑 및 배분작업 후, 자료 생성 및 추출하기 위해 사용한다.
	 *      ( OTC_SA_CB_DETAIL_UPLOAD 테이블 )
	 */
	public Collection SoaCBDiffAmtCreateProdedureCall(Long saNo, String vslCode, Long VoyNo, String chtInOut, Long stepNo,UserBean userBean, Connection conn) throws STXException {

		Collection result = null;


		try {
			DbWrap dbWrap = new DbWrap();
			DbWrap dbWrap2 = new DbWrap();			//table update 후 data 조회.

			StringBuffer sb = new StringBuffer();

			ArrayList inVariable = new ArrayList();


			inVariable.add(String.valueOf(vslCode));
			inVariable.add(VoyNo);
			inVariable.add(String.valueOf(chtInOut));
			inVariable.add(stepNo);
			inVariable.add(String.valueOf(saNo));
			inVariable.add(userBean.getUser_id());

			log.debug(">>>>>>>P_SET_SA_CB_DIFF_AMT_CREATE :---start");
			log.debug(">>>>>>>inVar.size()    : "+inVariable.size());

			// P_SET_SA_CB_DIFF_AMT_CREATE 프로시져 호출
			Object basicDatas[] = dbWrap.getObjectCstmt(conn,"{ call P_SET_SA_CB_DIFF_AMT_CREATE(?,?,?,?,?,?,?,?)  }",inVariable.toArray(),2);

			//Object objs[]       = dbwrap.getObjectCstmt(conn,sb.toString(),inVariable.toArray(),1);

			log.debug(">>>>>>>P_SET_SA_CB_DIFF_AMT_CREATE :---end");

			int row = 0;
			String msg = (String) basicDatas[row++];
			String msgcode = (String) basicDatas[row++];

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

				//result = dbWrap2.getObjects(conn, OTCSaCbDetailDTO.class, sb.toString());
				result = dbWrap2.getObjects(conn, OTCSaCbDetailUploadDTO.class, sb.toString());
			} else {
				log.error(">>>>>>P_SET_SA_CB_DIFF_AMT_CREATE   작업 시 오류 발생했습니다. ");
				// Error 메시지
				throw new STXException("[System Error] P_SET_SA_CB_DIFF_AMT_CREATE 작업 도중 오류가 발생했습니다.\n시스템 담당자에게 문의 바랍니다.");
			}


		} catch (Exception e) {

			throw new STXException(e);
		}
		return result;
	}


	/*
	 * CB에 의해 배분된 금액과 순수 SOA금액을 최종적으로 확인한다. 100% 일치해야함.
	 */
	public String SoaVsCBAmtCheck2(String saNo, Connection conn) throws Exception, STXException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = null;
		double locDiffAmt = 0;
		double usdDiffAmt = 0;
		long krwDiffAmt = 0;
		double saDurRate = 0;

		try {
			StringBuffer sb = new StringBuffer();

			sb.append("		-- 조정 대상 데이터만 추출															\n");
			sb.append("		  SELECT  A.DEVIDED_GROUP,       -- 배분된 GROUP KEY								\n");
			sb.append("				  MAX(A.VOY_NO) VOY_NO,  -- LAST 항차 의미 (조정할 대상 항차)				\n");
			sb.append("				  MAX(A.TRSACT_CODE) TRSACT_CODE,											\n");
			sb.append("				  -- SOA 원금																\n");
			sb.append("				  MAX(A.ORI_SA_RATE_DUR) ORI_SA_RATE_DUR,									\n");
			sb.append("				  MAX(A.ORI_USD_SA_AMT) ORI_USD_SA_AMT,										\n");
			sb.append("				  MAX(A.ORI_LOC_SA_AMT) ORI_LOC_SA_AMT,										\n");
			sb.append("				  MAX(A.ORI_KRW_SA_AMT) ORI_KRW_SA_AMT,										\n");
			sb.append("				  -- CB 로 배분된 값														\n");
			sb.append("				  SUM(A.SA_RATE_DUR) DIVIDED_SA_RATE_DUR,									\n");
			sb.append("				  SUM(A.USD_SA_AMT) DIVIDED_USD_SA_AMT,										\n");
			sb.append("				  SUM(A.LOC_SA_AMT) DIVIDED_LOC_SA_AMT,										\n");
			sb.append("				  SUM(A.KRW_SA_AMT) DIVIDED_KRW_SA_AMT ,									\n");
			sb.append("				  -- DIFF 값																\n");
			sb.append("				  MAX(A.ORI_SA_RATE_DUR) -  SUM(A.SA_RATE_DUR) as  DIFF_RATE_DUR,			\n");
			sb.append("				  MAX(A.ORI_USD_SA_AMT) -  SUM(A.USD_SA_AMT) as  USD_DIFF_AMT,				\n");
			sb.append("				  MAX(A.ORI_LOC_SA_AMT) -  SUM(A.LOC_SA_AMT) as  LOC_DIFF_AMT,				\n");
			sb.append("				  MAX(A.ORI_KRW_SA_AMT) -  SUM(A.KRW_SA_AMT) as  KRW_DIFF_AMT				\n");
			sb.append("		  FROM OTC_SA_CB_DETAIL_UPLOAD A															\n");
			sb.append("		  WHERE																				\n");
			sb.append("		  SA_NO = ?																		\n");
			sb.append("		  AND A.TRSACT_CODE IN ( 'A006', 'A007', 'A008', 'A009', 'H009', 'H010'				\n");
			// 2015.01월 데이터 생성작업을  위해서.. 구코드 임시로 넣음..!!(2015.01.28)
			sb.append("		  					   , 'A001', 'A002', 'A004', 'A005', 'H001', 'H002'	, 'A003'			\n");		//CVE추가 170327 GYJ
			sb.append("							   ---------------------------------------------------------	\n");
			sb.append("							   -- SPEED CLAIM 은 실채산 VOY_NO 를 직접 입력하므로,,			\n");
			sb.append("							   -- 채산항차로 배분하지 않는다 ( hijang 2015.01.05 )			\n");
			sb.append("							   ---------------------------------------------------------	\n");
			sb.append("							   --, 'I071', 'I072', 'I073'									\n");
			sb.append("								 )															\n");
			sb.append("		  GROUP BY A.DEVIDED_GROUP															\n");


			log.debug(" SoaVsCBAmtCheck : \n"+sb.toString());

			ps = conn.prepareStatement(sb.toString());

			int i = 1;
			ps.setString(i++, saNo);

			rs = ps.executeQuery();

			while (rs.next()) {
				saDurRate = rs.getDouble("DIFF_RATE_DUR");
				locDiffAmt = rs.getDouble("LOC_DIFF_AMT");
				usdDiffAmt = rs.getDouble("USD_DIFF_AMT");
				krwDiffAmt = rs.getLong("KRW_DIFF_AMT");

				log.debug("saDurRate : "+ saDurRate) ;
				log.debug("locDiffAmt : "+ locDiffAmt) ;
				log.debug("usdDiffAmt : "+ usdDiffAmt) ;
				log.debug("krwDiffAmt : "+ krwDiffAmt) ;

				if(saDurRate != 0 || locDiffAmt != 0 || usdDiffAmt != 0 || krwDiffAmt != 0){
					if (saDurRate != 0){
						result = "DUR";
					}
					if (locDiffAmt != 0){
						result ="LOC AMT";
					}

					if (usdDiffAmt != 0){
						result ="USD AMT";
					}

					if (krwDiffAmt != 0){
						result ="KRW AMT";
					}
				}else{
					result = "";
				}

			}

		} catch (Exception e) {
			throw new STXException(e);
		}finally{
			if(ps != null) ps.close();
		}
		return result;
	}

	/*
	 * SOA 입력 ITEM 들의 FROM-TO 기간에 대해서, 해당 채산항차 START-END DATE 에 걸치지 않은건 있는지 체크한다.
	 * 2014.12.23  HIJANG
	 */
	public ArrayList SoaVsCBDurationCheck2(String vslCode, Long VoyNo, String chtInOut, Long stepNo, UserBean userBean, Connection conn) throws Exception, STXException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList array = null;
		String invalid_item = "";

		try {
			StringBuffer sb = new StringBuffer();

			/*---------------------------------------------------------------------------------------------------------------------------------------
			sb.append("\n   SELECT M.DIVIDED_GROUP, M.TRSACT_CODE,                            				");
			sb.append("\n   	   TRSACT_NAME_FUNC('SOMO', 'T', M.TRSACT_CODE ) TRSACT_NAME,  				");
			sb.append("\n   	   M.FAIL_ITEM, M.PASS_ITEM                                   				");
			sb.append("\n   FROM                                                                                                   ");
			sb.append("\n   (                                                                                                      ");
			sb.append("\n     SELECT T.DIVIDED_GROUP, T.TRSACT_CODE, MAX(T.FAIL_ITEM) FAIL_ITEM, MAX(T.PASS_ITEM) PASS_ITEM        ");
			sb.append("\n     FROM                                                                                                 ");
			sb.append("\n     (                                                                                                    ");
			sb.append("\n       SELECT K.DIVIDED_GROUP, K.TRSACT_CODE, DECODE(K.IS_VALID,'FAIL','FAIL','') AS FAIL_ITEM, DECODE(K.IS_VALID,'PASS','PASS','') AS PASS_ITEM  ");
			sb.append("\n       FROM                                                              ");
			sb.append("\n       (                                                                 ");
			sb.append("\n         SELECT S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID               ");
			sb.append("\n         FROM                                                            ");
			sb.append("\n         (                                                               ");
			sb.append("\n             SELECT SOA.*,                                               ");
			sb.append("\n                    CB.*,                                                ");
			sb.append("\n                      CASE WHEN ( ( SOA.RECALC_FROM_DATE >= CB.VOY_STRT_DATE  and  SOA.RECALC_FROM_DATE <=  CB.VOY_END_DATE )       ");
			sb.append("\n                                 or ( SOA.RECALC_TO_DATE >= CB.VOY_STRT_DATE  and  SOA.RECALC_TO_DATE <=  CB.VOY_END_DATE )         ");
			sb.append("\n                                 or ( CB.VOY_STRT_DATE >= SOA.RECALC_FROM_DATE  and  CB.VOY_STRT_DATE <=  SOA.RECALC_TO_DATE )      ");
			sb.append("\n                                 or ( CB.VOY_END_DATE >= SOA.RECALC_FROM_DATE  and  CB.VOY_END_DATE <=  SOA.RECALC_TO_DATE )        ");
			sb.append("\n                                ) THEN 'PASS'                                                                                       ");
			sb.append("\n                           WHEN   ( SOA.RECALC_FROM_DATE IS NULL OR SOA.RECALC_TO_DATE IS NULL ) THEN 'PASS'   ");		// SOA 기간 없는건 -> 무조건 PASS 시킨다
			sb.append("\n                           ELSE 'FAIL'                                   						 ");
			sb.append("\n                           END IS_VALID                                  						 ");
			sb.append("\n             FROM                                                        						 ");
			sb.append("\n             (                                                           						 ");
			sb.append("\n                   SELECT    H.SA_NO, H.VSL_CODE, H.VOY_NO, DECODE(H.CHT_IN_OUT_CODE,'T','T', 'C','T', 'O') AS CHT_IN_OUT_CODE,     ");
			sb.append("\n                              H.STEP_NO, H.CNTR_NO,                                             ");
			sb.append("\n                              D.TRSACT_CODE,                                                    ");
			sb.append("\n                              D.SA_RATE_DUR, D.LOC_SA_AMT,  D.USD_SA_AMT, D.KRW_SA_AMT,         ");
			sb.append("\n                              D.FROM_DATE,                                                      ");
			sb.append("\n                              D.TO_DATE,                                                        ");
			sb.append("\n                              CASE WHEN( D.FROM_DATE <= D.TO_DATE ) THEN                        ");
			sb.append("\n                                                D.FROM_DATE                                     ");
			sb.append("\n                                            ELSE                                                ");
			sb.append("\n                                                D.TO_DATE                                       ");
			sb.append("\n                                            END RECALC_FROM_DATE,                               ");
			sb.append("\n                                     CASE WHEN( D.FROM_DATE <= D.TO_DATE ) THEN                 ");
			sb.append("\n                                                D.TO_DATE                                       ");
			sb.append("\n                                            ELSE                                                ");
			sb.append("\n                                                D.FROM_DATE                                     ");
			sb.append("\n                                            END RECALC_TO_DATE,                                 ");
			sb.append("\n                              ROWNUM  AS DIVIDED_GROUP         								 "); 	// 배분된 항차를 찾는 Group Key
			sb.append("\n                   FROM OTC_SA_HEAD H, OTC_SA_DETAIL D                                          ");
			sb.append("\n                   WHERE                                                                        ");
			sb.append("\n                       H.SA_NO = D.SA_NO                                                        ");
			sb.append("\n                   AND H.STEP_NO <> 0                                                           ");
			sb.append("\n                   AND D.TRSACT_CODE IN  ( 'A006', 'A007', 'A008', 'A009',   					 ");
			sb.append("\n                   						'H009', 'H010', 'I071', 'I072', 'I073'	     		 ");
			// 2015.01월 데이터 생성작업을  위해서.. 구코드 임시로 넣음..!!(2015.01.28)
			sb.append("\n                   					   ,'A001', 'A002', 'A004', 'A005',     	   		 	 ");
			sb.append("\n                   						'H001', 'H002', 'I003', 'I004', 'I005'  )     		 ");
			sb.append("\n                   AND D.USD_SA_AMT <> 0                           ");
			// 테스트 데이터
			sb.append("\n	        		AND H.VSL_CODE = ?                              ");
			sb.append("\n	        		AND H.VOY_NO = ?                                ");
			sb.append("\n	        		AND H.CHT_IN_OUT_CODE = ?                       ");
			sb.append("\n	        		AND H.STEP_NO = ?                               ");
			sb.append("\n             ) SOA,                                                ");
			//sb.append("\n             CB_ALL_VOY_INFO_V CB                                ");
			sb.append("\n             CB_ALL_VOY_INFO_ADJUST_V CB                           ");	 // 기간 중첩되는 채산항차 처리 (2015.01.03)
			sb.append("\n             WHERE                                                 ");
			sb.append("\n                   SOA.VSL_CODE = CB.VSL_CODE                      ");
			sb.append("\n             AND SOA.CHT_IN_OUT_CODE = CB.VOYAGE_FLAG              ");
			sb.append("\n             AND SOA.CNTR_NO = CB.CNTR_NO               			");	 // 계약번호까지 JOIN 걸어야 됨. (hijang 2015.01.15)
			sb.append("\n         ) S                                                       ");
			sb.append("\n         GROUP BY S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID       ");
			sb.append("\n         ORDER BY S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID       ");
			sb.append("\n       ) K                                                         ");
			sb.append("\n     ) T                                                           ");
			sb.append("\n     GROUP BY T.DIVIDED_GROUP, T.TRSACT_CODE                       ");
			sb.append("\n     ORDER BY T.DIVIDED_GROUP, T.TRSACT_CODE                       ");
			sb.append("\n   ) M                                                             ");
			sb.append("\n   WHERE                                                           ");
			sb.append("\n   M.FAIL_ITEM = 'FAIL' AND M.PASS_ITEM IS NULL                 	");	// 기간 오류 ITEM 만 추출 (FAIL 건만 존재하는건 추출)
			sb.append("\n   ORDER BY M.DIVIDED_GROUP, M.TRSACT_CODE                         ");
			---------------------------------------------------------------------------------------------------------------------------------------*/

			// GMT 와 LOCAL 시간 - DIFF 차이 처리용 ( hijang 2015.02.09 )
			sb.append("\n   SELECT M.DIVIDED_GROUP, M.TRSACT_CODE,                                    												");
			sb.append("\n          TRSACT_NAME_FUNC('SOMO', 'T', M.TRSACT_CODE ) TRSACT_NAME,                                                       ");
			sb.append("\n          M.FAIL_ITEM, M.PASS_ITEM                                                                                         ");
			sb.append("\n   FROM                                                                                                                    ");
			sb.append("\n   (                                                                                                                       ");
			sb.append("\n     SELECT T.DIVIDED_GROUP, T.TRSACT_CODE, MAX(T.FAIL_ITEM) FAIL_ITEM, MAX(T.PASS_ITEM) PASS_ITEM                         ");
			sb.append("\n     FROM                                                                                                                  ");
			sb.append("\n     (                                                                                                                     ");
			sb.append("\n       SELECT K.DIVIDED_GROUP, K.TRSACT_CODE, DECODE(K.IS_VALID,'FAIL','FAIL','') AS FAIL_ITEM, DECODE(K.IS_VALID,'PASS','PASS','') AS PASS_ITEM  ");
			sb.append("\n       FROM                                                                                                                 ");
			sb.append("\n       (                                                                                                                    ");
			sb.append("\n         SELECT S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID                                                                  ");
			sb.append("\n         FROM                                                                                                               ");
			sb.append("\n         (                                                                                                                  ");
			sb.append("\n              SELECT SS.DIVIDED_GROUP, SS.TRSACT_CODE,                                                                      ");
			sb.append("\n                     SS.RECALC_FROM_DATE, SS.RECALC_TO_DATE,                                                                ");
			sb.append("\n                     SS.VOY_STRT_DATE, SS.VOY_END_DATE,                                                                     ");
			sb.append("\n                     -- VALID 여부 체크..                                                                                   	 ");
			sb.append("\n                     CASE WHEN ( ( SS.RECALC_FROM_DATE >= SS.VOY_STRT_DATE  and  SS.RECALC_FROM_DATE <=  SS.VOY_END_DATE )  ");
			sb.append("\n                               or ( SS.RECALC_TO_DATE >= SS.VOY_STRT_DATE  and  SS.RECALC_TO_DATE <=  SS.VOY_END_DATE )     ");
			sb.append("\n                               or ( SS.VOY_STRT_DATE >= SS.RECALC_FROM_DATE  and  SS.VOY_STRT_DATE <=  SS.RECALC_TO_DATE )  ");
			sb.append("\n                               or ( SS.VOY_END_DATE >= SS.RECALC_FROM_DATE  and  SS.VOY_END_DATE <=  SS.RECALC_TO_DATE )    ");
			sb.append("\n                              ) THEN 'PASS'                                                                                 ");
			sb.append("\n                         WHEN   ( SS.RECALC_FROM_DATE IS NULL OR SS.RECALC_TO_DATE IS NULL ) THEN 'PASS'       			 ");	// SOA 기간 없는건 -> 무조건 PASS 시킨다
			sb.append("\n                         ELSE 'FAIL'                                                          ");
			sb.append("\n                         END IS_VALID                                                         ");
			sb.append("\n              FROM                                                                            ");
			sb.append("\n              (                                                                               ");
			sb.append("\n                  SELECT ZZ.DIVIDED_GROUP, ZZ.TRSACT_CODE,                                    ");
			sb.append("\n                         ZZ.RECALC_FROM_DATE, ZZ.RECALC_TO_DATE,                              ");
			sb.append("\n                         --ZZ.VOY_STRT_DATE, ZZ.VOY_END_DATE,                                 ");
			//----------------------------------------------------------------------------------------------------
			//-- SOA와 채산상의 시간 DIFF(GMT 와 LOCAL 시간) 로 인하여,
			//-- 기존 채산 VOY_STRT_DATE 와 VOY_END_DATE 를 +/- 12H 만큼씩 보정해줌 !! ( HIJANG 2015.02.09 )
			//---------------------------------------------------------------------------------------------------
			sb.append("\n                          CASE WHEN ( ZZ.SOA_TO_CB_STRT_DIFF < 0 AND ZZ.SOA_TO_CB_STRT_DIFF >= -0.5 ) THEN		");
			sb.append("\n                                    DECODE( CNT, 0, ZZ.VOY_STRT_DATE - 0.5, ZZ.VOY_STRT_DATE )	");	// (-) 12시간 보정
			sb.append("\n                               ELSE                                                          	");
			sb.append("\n                                    ZZ.VOY_STRT_DATE                                         	");
			sb.append("\n                               END VOY_STRT_DATE,                                            	");
			sb.append("\n                          CASE WHEN ( ZZ.SOA_FROM_CB_END_DIFF > 0 AND ZZ.SOA_FROM_CB_END_DIFF <= 0.5 ) THEN       	");
			sb.append("\n                                    DECODE( CNT, 0, ZZ.VOY_END_DATE + 0.5, ZZ.VOY_END_DATE )	");  // (+) 12시간 보정
			sb.append("\n                               ELSE                                                          	");
			sb.append("\n                                    ZZ.VOY_END_DATE                                          	");
			sb.append("\n                               END VOY_END_DATE                                              	");
			sb.append("\n                  FROM                                                                       	");
			sb.append("\n                  (                                                                          	");
			sb.append("\n                      SELECT Z.DIVIDED_GROUP, Z.TRSACT_CODE,                                 	");
			sb.append("\n                             Z.RECALC_FROM_DATE, Z.RECALC_TO_DATE,                           	");
			sb.append("\n                             Z.VOY_STRT_DATE, Z.VOY_END_DATE,                                	");
			//SOA 기간(FROM-TO) 이 채산항차 기간( VOY_STRT_DATE,VOY_END_DATE) 에 포함된 갯수 체크용 ( hijang 2015.02.10 )
			sb.append("\n                             DECODE( Z.RECALC_FROM_DATE, NULL, 0, SOA_CB_DURATION_INCLUDE_CNT(Z.CNTR_NO, Z.RECALC_FROM_DATE, Z.RECALC_TO_DATE) ) AS CNT,  	");
			sb.append("\n                             ( Z.RECALC_TO_DATE - Z.VOY_STRT_DATE ) as SOA_TO_CB_STRT_DIFF,   	"); // '-0.5DAY ~ 0' 이내 존재시,, --> -12h 보정 필요
			sb.append("\n                             ( Z.RECALC_FROM_DATE - Z.VOY_END_DATE ) as SOA_FROM_CB_END_DIFF  	"); // '0 ~ +0.5DAY' 이내 존재시,, --> +12h 보정 필요
			sb.append("\n                      FROM                                                                     ");
			sb.append("\n                      (                                                                        ");
			sb.append("\n                           SELECT SOA.CNTR_NO, SOA.TRSACT_CODE,                                ");
			sb.append("\n                                  to_date(to_char(SOA.RECALC_FROM_DATE,'yyyymmddhh24mi'),'yyyymmddhh24miss') RECALC_FROM_DATE,        ");
			sb.append("\n                                  to_date(to_char(SOA.RECALC_TO_DATE,'yyyymmddhh24mi'), 'yyyymmddhh24miss') RECALC_TO_DATE,        ");
			sb.append("\n                                  SOA.DIVIDED_GROUP,      										");
			sb.append("\n                                  to_date(to_char(CB.VOY_STRT_DATE,'yyyymmddhh24mi'),'yyyymmddhh24miss') as VOY_STRT_DATE,       ");
			sb.append("\n                                  to_date(to_char(CB.VOY_END_DATE,'yyyymmddhh24mi'),'yyyymmddhh24miss') as VOY_END_DATE,       ");
			sb.append("\n                                  CB.VOYAGE_FLAG      											");
			sb.append("\n                           FROM                                                                ");
			sb.append("\n                           (                                                                   ");
			sb.append("\n                                 SELECT    H.SA_NO, H.VSL_CODE, H.VOY_NO, DECODE(H.CHT_IN_OUT_CODE,'T','T', 'C','T', 'O') AS CHT_IN_OUT_CODE,   ");
			sb.append("\n                                            H.STEP_NO, H.CNTR_NO,                                        ");
			sb.append("\n                                            D.TRSACT_CODE,                                               ");
			sb.append("\n                                            D.SA_RATE_DUR, D.LOC_SA_AMT,  D.USD_SA_AMT, D.KRW_SA_AMT,    ");
			sb.append("\n                                            D.FROM_DATE,                                                 ");
			sb.append("\n                                            D.TO_DATE,                                                   ");
			sb.append("\n                                            CASE WHEN( D.FROM_DATE <= D.TO_DATE ) THEN                   ");
			sb.append("\n                                                              D.FROM_DATE                                ");
			sb.append("\n                                                          ELSE                                           ");
			sb.append("\n                                                              D.TO_DATE                                  ");
			sb.append("\n                                                          END RECALC_FROM_DATE,                          ");
			sb.append("\n                                                   CASE WHEN( D.FROM_DATE <= D.TO_DATE ) THEN            ");
			sb.append("\n                                                              D.TO_DATE                                  ");
			sb.append("\n                                                          ELSE                                           ");
			sb.append("\n                                                              D.FROM_DATE                                ");
			sb.append("\n                                                          END RECALC_TO_DATE,                            ");
			sb.append("\n                                            ROWNUM  AS DIVIDED_GROUP                             				");	// 배분된 항차를 찾는 Group Key
			sb.append("\n                                 FROM OTC_SA_HEAD H, OTC_SA_DETAIL D                                     ");
			sb.append("\n                                 WHERE                                                                   ");
			sb.append("\n                                     H.SA_NO = D.SA_NO                                                   ");
			sb.append("\n                                 AND H.STEP_NO <> 0                                                      ");
			sb.append("\n                                 AND D.TRSACT_CODE IN  ( 'A006', 'A007', 'A008', 'A009',                 ");
			sb.append("\n                                             'H009', 'H010', 'I071', 'I072', 'I073'                      ");
			/// 2015.01월 데이터 생성작업을  위해서.. 구코드 임시로 넣음..!!(2015.01.28)
			sb.append("\n                                              ,'A001', 'A002', 'A004', 'A005',                           ");
			sb.append("\n                                             'H001', 'H002', 'I003', 'I004', 'I005'  )                   ");
			sb.append("\n                                 AND D.USD_SA_AMT <> 0                           						  ");
			sb.append("\n                                 -- 테스트 데이터                                											  ");
			sb.append("\n                                 AND H.VSL_CODE = ?                        							");
			sb.append("\n                                 AND H.VOY_NO = ?                               						");
			sb.append("\n                                 AND H.CHT_IN_OUT_CODE = ?                     						");
			sb.append("\n                                 AND H.STEP_NO = ?                               						");
			sb.append("\n                                                                                 						");
			sb.append("\n                           ) SOA,                                                						");
			sb.append("\n                           CB_ALL_VOY_INFO_ADJUST_V CB                           						");  // 기간 중첩되는 채산항차 처리 (2015.01.03)
			sb.append("\n                           WHERE                                                 						");
			sb.append("\n                                 SOA.VSL_CODE = CB.VSL_CODE                      						");
			sb.append("\n                           AND SOA.CHT_IN_OUT_CODE = CB.VOYAGE_FLAG              						");
			sb.append("\n                           AND SOA.CNTR_NO = CB.CNTR_NO                          						");
			sb.append("\n                      ) Z                                                        						");
			sb.append("\n                 ) ZZ                                                            						");
			sb.append("\n             ) SS                                                                						");
			sb.append("\n         ) S                                                       			");
			sb.append("\n         GROUP BY S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID         			");
			sb.append("\n         ORDER BY S.DIVIDED_GROUP, S.TRSACT_CODE, S.IS_VALID         			");
			sb.append("\n       ) K                                                           			");
			sb.append("\n     ) T                                                             			");
			sb.append("\n     GROUP BY T.DIVIDED_GROUP, T.TRSACT_CODE                         			");
			sb.append("\n     ORDER BY T.DIVIDED_GROUP, T.TRSACT_CODE                         			");
			sb.append("\n   ) M                                                               			");
			sb.append("\n   WHERE                                                             			");
			sb.append("\n   M.FAIL_ITEM = 'FAIL' AND M.PASS_ITEM IS NULL                     			"); 	// 기간 오류 ITEM 만 추출 (FAIL 건만 존재하는건 추출)
			sb.append("\n   ORDER BY M.DIVIDED_GROUP, M.TRSACT_CODE                         			");


			log.debug(" SoaVsCBDurationCheck2 : \n"+sb.toString());

			ps = conn.prepareStatement(sb.toString());

			int i = 1;
			ps.setString(i++, vslCode);
			ps.setLong(i++, VoyNo.longValue());
			ps.setString(i++, chtInOut);
			ps.setLong(i++, stepNo.longValue());

			rs = ps.executeQuery();

			array = new ArrayList();

			while (rs.next()) {
				invalid_item = rs.getString("TRSACT_NAME");
				//log.debug("invalid_item : "+ invalid_item) ;

				array.add(invalid_item);
			}

		} catch (Exception e) {
			throw new STXException(e);
		}finally{
			if(ps != null) ps.close();
		}
		return array;
	}

	public String getCBmaxVoyNo(String vslCode, String cntrNo, String chtInOut, Connection conn) throws STXException {



		PreparedStatement ps = null;
		ResultSet rs = null;
		String cbVoy = "";
		try {

			StringBuffer sb = new StringBuffer();
			sb.append("select max(a.VSL_CODE) cb_vsl_code,																	\n");
			sb.append("       max(a.VOY_NO) cb_voy,																			\n");
			sb.append("       b.cntr_no,																					\n");
			sb.append("       b.cht_in_out_code,																			\n");
			sb.append("       b.vsl_code sa_vsl_code,																		\n");
			sb.append("       b.voy_no																						\n");
			sb.append("  from CB_ALL_VOY_INFO_ADJUST_V a, otc_sa_head b					 									\n");
			sb.append(" where a.VSL_CODE(+) = b.vsl_code															 		\n");
			sb.append("   and a.CNTR_NO(+) = b.cntr_no																 		\n");
			sb.append("   and a.VOYAGE_FLAG(+) = decode(b.cht_in_out_code, 'T', 'T', 'O')									\n");
			sb.append("   and b.VSL_CODE = '"+vslCode+"'																	\n");
			sb.append("   and b.CNTR_NO = '"+cntrNo+"'																		\n");
			sb.append("   and b.cht_in_out_code = '"+chtInOut+"'															\n");
			sb.append(" group by b.cntr_no, b.cht_in_out_code, b.vsl_code, b.voy_no			 								\n");

			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				cbVoy = rs.getString("cb_voy");

			}


		} catch (Exception e) {

			throw new STXException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e1) {

				throw new STXException(e1);
			}
		}
		return cbVoy;
	}


	// Ballast 채산관련 SOA 선급금 생성 후, 다음 STEP 에서 처리
	// 이전STEP에서 발생시킨 선급(J010) 정산여부 체크
	public String isBallastPendingAmt(String saNo, String vslCode, Long voyNo, String posting_date, Connection conn) throws STXException {

		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		int cnt = 0;
		int cnt2 = 0;
		String isPending = "N";
		String result = "";
		String bigResult = "";

		try {

log.debug("saNo : "+ saNo) ;
log.debug("vslCode : "+ vslCode) ;
log.debug("voyNo : "+ voyNo) ;
log.debug("posting_date : "+ posting_date) ;

			StringBuffer sb = new StringBuffer();
			String v_trx_number = "";

			sb.append(" select TRX_NUMBER												\n");
			sb.append("  from EAR_IF_TRX_BALANCE_V				 						\n");
			sb.append(" where SOURCE_SYSTEM = 'SOMO' 									\n");
			sb.append("   AND gl_date  <= to_date( ? ,'yyyymmdd') 						\n");  // 해당 STEP 의 'GL_DATE'
			sb.append("   AND SUBSTR(IF_TYPE_ID,3,5) = 'J010'							\n");  // PREPAYMENT(Ballast)
			sb.append("   AND SEGMENT3 = '110902' 										\n");  // 선급금
			sb.append("   AND SEGMENT4 = ? 												\n");
			sb.append("   and source_trx_number in ( select T.SA_NO from otc_sa_head T WHERE T.CNTR_NO IN (select M.CNTR_NO from otc_sa_head m where m.sa_no = ?) ) 	\n");
			sb.append("   AND (nvl(ENTERED_BALANCE_AMOUNT,0) - nvl(ENTERED_PENDING_AMOUNT,0))  <>  0  			\n");

			log.debug(" isBallastPendingAmt : \n"+sb.toString());

			ps = conn.prepareStatement(sb.toString());

			int i = 1;
			ps.setString(i++, posting_date); 	// GL_DATE
			ps.setString(i++, vslCode);			// 선박코드
			ps.setString(i++, saNo);			// SA_NO

			rs = ps.executeQuery();

			while ( rs.next() ) {

				v_trx_number = rs.getString("TRX_NUMBER");

				sb = new StringBuffer();
				sb.append("select count(*) as item_cnt											\n");
				sb.append("  from OTC_SA_DETAIL						 							\n");
				sb.append(" where SA_NO = ? 													\n");
				sb.append("   AND TRSACT_CODE = 'I030' 				\n");   // ACTUAL OWNER'S A/C 정산
				//sb.append("   AND USD_SA_AMT <> 0												\n");
				sb.append("   AND STL_ERP_SLIP_NO = ?											\n");

				log.debug(" isBallastPendingAmt : \n"+sb.toString());

				ps2 = conn.prepareStatement(sb.toString());

				int j = 1;
				ps2.setString(j++, saNo); // SA_NO
				ps2.setString(j++, v_trx_number); // TRX_NUMBER(정산번호)

				rs2 = ps2.executeQuery();

				if ( rs2.next() ) {

					cnt2 = rs2.getInt("item_cnt");
					log.debug("cnt2 : "+ cnt2) ;

					if(cnt2 > 0){
						result = "Y";
					}else{
						result = "N";
					}
				}


				bigResult = bigResult + result ;
			}


		} catch (Exception e) {
			throw new STXException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (rs2 != null)
					rs2.close();
				if (ps != null)
					ps.close();
				if (ps2 != null)
					ps2.close();
			} catch (SQLException e1) {
				throw new STXException(e1);
			}
		}

		return bigResult;
	}



	public String getOnHireMinFromDate(String saNo, Connection conn) throws STXException {

		String result = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringBuffer sb = new StringBuffer();

			// Query 가져오기
			sb.append("SELECT TO_CHAR(MIN(A.FROM_DATE),'YYYYMMDD') MIN_FROM_DATE 		\n");
			sb.append("  FROM OTC_SA_DETAIL A		 									\n");
			sb.append(" WHERE A.SA_NO = '"+saNo+"'		 								\n");
			sb.append("   AND A.TRSACT_CODE IN ('A006')	 								\n");
			sb.append("   AND A.USD_SA_AMT <> 0		 									\n");
			sb.append(" GROUP BY A.TRSACT_CODE		 									\n");


			log.debug("getOnHireMinFromDate : " + sb.toString());

    		ps = conn.prepareStatement(sb.toString());
   		    rs = ps.executeQuery();

			if (rs.next()) {
				result = rs.getString("min_from_date");
			}

		} catch (Exception e) {
			throw new STXException(e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException e1) {

				throw new STXException(e1);
			}
		}
		return result;
	}


}
