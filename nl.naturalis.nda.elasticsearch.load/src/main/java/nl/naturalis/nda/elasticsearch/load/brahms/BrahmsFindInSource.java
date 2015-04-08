package nl.naturalis.nda.elasticsearch.load.brahms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

public class BrahmsFindInSource {

	static String FileCSVnm = null;
	private int bulkRequestSize = 1000;


	public static void main(String[] args) throws Exception
	{
		BrahmsFindInSource brahms = new BrahmsFindInSource();
		String brahmsCsvDir = LoadUtil.getConfig().required("brahms.csv_dir");

		File file = new File(brahmsCsvDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", brahmsCsvDir));
		}
		File[] csvFiles = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".csv");
			}
		});

		for (File f : csvFiles) {
			if (f.isFile()) {
				System.out.println(f.getName());
				System.out.println(f.getAbsolutePath());
				FileCSVnm = f.getName();
			}
			System.out.println(brahms.ReadValueFromCsv(f.getAbsolutePath()));
		}

	}


	public List<BrahmsMembers> ReadValueFromCsv(String path) throws IOException
	{
		LineNumberReader br = new LineNumberReader(new FileReader(path));
		List<BrahmsMembers> brahmsList = new ArrayList<>(bulkRequestSize);
		String line = null;
		try {
			int count = 0;
			br.readLine();
			while ((line = br.readLine()) != null) {
				BrahmsMembers brahmsmemb = new BrahmsMembers();
				Scanner scanner = new Scanner(line);
				scanner.useDelimiter(",");
				while (scanner.hasNext()) {
					//System.out.println("" + scanner.hasNext());

					String data = scanner.next();
					switch (count) {
						case 0:
							brahmsmemb.setTAG(data);
							break;
						case 1:
							brahmsmemb.setDEL(data);
							break;
						case 2:
							brahmsmemb.setHERBARIUM(data);
							break;
						case 3:
							brahmsmemb.setCATEGORY(data);
							break;
						case 4:
							brahmsmemb.setSPECID(data);
							break;
						case 5:
							brahmsmemb.setBRAHMS(data);
							break;
						case 6:
							brahmsmemb.setACCESSION(data);
							break;
						case 7:
							brahmsmemb.setBARCODE(data);
							break;
						case 8:
							brahmsmemb.setOLDBARCODE(data);
							break;
						case 9:
							brahmsmemb.setPHENOLOGY(data);
							break;
						case 10:
							brahmsmemb.setCOLLECTOR(data);
							break;
						case 11:
							brahmsmemb.setPREFIX(data);
							break;
						case 12:
							brahmsmemb.setNUMBER(data);
							break;
						case 13:
							brahmsmemb.setSUFFIX(data);
							break;
						case 14:
							brahmsmemb.setADDCOLLALL(data);
							break;
						case 15:
							brahmsmemb.setADDCOLLALL(data);
							break;
						case 16:
							brahmsmemb.setTYPE(data);
							break;
						case 17:
							brahmsmemb.setTYPE_OF(data);
							break;
						case 18:
							brahmsmemb.setTYPEURL(data);
							break;
						case 19:
							brahmsmemb.setDAY(data);
							break;
						case 20:
							brahmsmemb.setMONTH(data);
							break;
						case 21:
							brahmsmemb.setYEAR(data);
							break;
						case 22:
							brahmsmemb.setDATERES(data);
							break;
						case 23:
							brahmsmemb.setFAMCLASS(data);
							break;
						case 24:
							brahmsmemb.setORDER(data);
							break;
						case 25:
							brahmsmemb.setFAMILY(data);
							break;
						case 26:
							brahmsmemb.setCF(data);
							break;
						case 27:
							brahmsmemb.setTAXSTAT(data);
							break;
						case 28:
							brahmsmemb.setSPECIES(data);
							break;
						case 29:
							brahmsmemb.setDETSTATUS(data);
							break;
						case 30:
							brahmsmemb.setDETBY(data);
							break;
						case 31:
							brahmsmemb.setDAYIDENT(data);
							break;
						case 32:
							brahmsmemb.setMONTHIDENT(data);
							break;
						case 33:
							brahmsmemb.setYEARIDENT(data);
							break;
						case 34:
							brahmsmemb.setDETDATE(data);
							break;
						case 35:
							brahmsmemb.setDETHISTORY(data);
							break;
						case 36:
							brahmsmemb.setDETNOTES(data);
							break;
						case 37:
							brahmsmemb.setCURATENOTE(data);
							break;
						case 38:
							brahmsmemb.setORIGINSTAT(data);
							break;
						case 39:
							brahmsmemb.setORIGINID(data);
							break;
						case 40:
							brahmsmemb.setCONTINENT(data);
							break;
						case 41:
							brahmsmemb.setREGION(data);
							break;
						case 42:
							brahmsmemb.setCOUNTRY(data);
							break;
						case 43:
							brahmsmemb.setMAJORAREA(data);
							break;
						case 44:
							brahmsmemb.setMINORAREA(data);
							break;
						case 45:
							brahmsmemb.setLOCPREFIX(data);
							break;
						case 46:
							brahmsmemb.setGAZETTEER(data);
							break;
						case 47:
							brahmsmemb.setLOCNOTES(data);
							break;
						case 48:
							brahmsmemb.setHABITATTXT(data);
							break;
						case 49:
							brahmsmemb.setNOTE(data);
							break;
						case 50:
							brahmsmemb.setCULTNOTES(data);
							break;
						case 51:
							brahmsmemb.setLATITUDE(data);
							break;
						case 52:
							brahmsmemb.setNS(data);
							break;
						case 53:
							brahmsmemb.setLONGITUDE(data);
							break;
						case 54:
							brahmsmemb.setEW(data);
							break;
						case 55:
							brahmsmemb.setLLUNIT(data);
							break;
						case 56:
							brahmsmemb.setLLRES(data);
							break;
						case 57:
							brahmsmemb.setLLORIG(data);
							break;
						case 58:
							brahmsmemb.setLATLONG(data);
							break;
						case 59:
							brahmsmemb.setLATDEC(data);
							break;
						case 60:
							brahmsmemb.setLONGDEC(data);
							break;
						case 61:
							brahmsmemb.setDEGSQ(data);
							break;
						case 62:
							brahmsmemb.setMINEL(data);
							break;
						case 63:
							brahmsmemb.setMAXELEV(data);
							break;
						case 64:
							brahmsmemb.setALTRES(data);
							break;
						case 65:
							brahmsmemb.setALTTEXT(data);
							break;
						case 66:
							brahmsmemb.setALTRANGE(data);
							break;
						case 67:
							brahmsmemb.setGEODATA(data);
							break;
						case 68:
							brahmsmemb.setPLANTDESC(data);
							break;
						case 69:
							brahmsmemb.setNOTES(data);
							break;
						case 70:
							brahmsmemb.setVERNACULAR(data);
							break;
						case 71:
							brahmsmemb.setLANGUAGE(data);
							break;
						case 72:
							brahmsmemb.setGENUS(data);
							break;
						case 73:
							brahmsmemb.setSP1(data);
							break;
						case 74:
							brahmsmemb.setAUTHOR1(data);
							break;
						case 75:
							brahmsmemb.setRANK1(data);
							break;
						case 76:
							brahmsmemb.setSP2(data);
							break;
						case 77:
							brahmsmemb.setAUTHOR2(data);
							break;
						case 78:
							brahmsmemb.setRANK2(data);
							break;
						case 79:
							brahmsmemb.setSP3(data);
							break;
						case 80:
							brahmsmemb.setAUTHOR3(data);
							break;
						case 81:
							brahmsmemb.setUNIQUE(data);
							break;
						case 82:
							brahmsmemb.setHSACCODE(data);
							break;
						case 83:
							brahmsmemb.setGAZCODE(data);
							break;
						case 84:
							brahmsmemb.setHBCODE(data);
							break;
						case 85:
							brahmsmemb.setHSTYPE(data);
							break;
						case 86:
							brahmsmemb.setSPTYPE(data);
							break;
						case 87:
							brahmsmemb.setSPNUMBER(data);
							break;
						case 88:
							brahmsmemb.setSPCODETYPE(data);
							break;
						case 89:
							brahmsmemb.setCSCODE(data);
							break;
						case 90:
							brahmsmemb.setALTCS(data);
							break;
						case 91:
							brahmsmemb.setADDCSCODE(data);
							break;
						case 92:
							brahmsmemb.setDETBYCODE(data);
							break;
						case 93:
							brahmsmemb.setCONUMBER(data);
							break;
						case 94:
							brahmsmemb.setCCID(data);
							break;
						case 95:
							brahmsmemb.setENTRYDATE(data);
							break;
						case 96:
							brahmsmemb.setWHO(data);
							break;
						case 97:
							brahmsmemb.setNOTONLINE(data);
							break;
						case 98:
							brahmsmemb.setDATELASTM(data);
							break;
						case 99:
							brahmsmemb.setIMAGELIST(data);
							break;
						default:
							break;

					}
					count++;
					//System.out.println("" + scanner.nextLine());
				}

				count = 0;
				brahmsList.add(brahmsmemb);
				System.out.println(brahmsList);
				scanner.close();

			}
			br.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("Eror: " + e);
		}
		return brahmsList;
	}

	public class BrahmsMembers {
		public String getTAG()
		{
			return TAG;
		}


		public void setTAG(String tAG)
		{
			TAG = tAG;
		}


		public String getDEL()
		{
			return DEL;
		}


		public void setDEL(String dEL)
		{
			DEL = dEL;
		}


		public String getHERBARIUM()
		{
			return HERBARIUM;
		}


		public void setHERBARIUM(String hERBARIUM)
		{
			HERBARIUM = hERBARIUM;
		}


		public String getCATEGORY()
		{
			return CATEGORY;
		}


		public void setCATEGORY(String cATEGORY)
		{
			CATEGORY = cATEGORY;
		}


		public String getSPECID()
		{
			return SPECID;
		}


		public void setSPECID(String sPECID)
		{
			SPECID = sPECID;
		}


		public String getBRAHMS()
		{
			return BRAHMS;
		}


		public void setBRAHMS(String bRAHMS)
		{
			BRAHMS = bRAHMS;
		}


		public String getACCESSION()
		{
			return ACCESSION;
		}


		public void setACCESSION(String aCCESSION)
		{
			ACCESSION = aCCESSION;
		}


		public String getBARCODE()
		{
			return BARCODE;
		}


		public void setBARCODE(String bARCODE)
		{
			BARCODE = bARCODE;
		}


		public String getOLDBARCODE()
		{
			return OLDBARCODE;
		}


		public void setOLDBARCODE(String oLDBARCODE)
		{
			OLDBARCODE = oLDBARCODE;
		}


		public String getPHENOLOGY()
		{
			return PHENOLOGY;
		}


		public void setPHENOLOGY(String pHENOLOGY)
		{
			PHENOLOGY = pHENOLOGY;
		}


		public String getCOLLECTOR()
		{
			return COLLECTOR;
		}


		public void setCOLLECTOR(String cOLLECTOR)
		{
			COLLECTOR = cOLLECTOR;
		}


		public String getPREFIX()
		{
			return PREFIX;
		}


		public void setPREFIX(String pREFIX)
		{
			PREFIX = pREFIX;
		}


		public String getNUMBER()
		{
			return NUMBER;
		}


		public void setNUMBER(String nUMBER)
		{
			NUMBER = nUMBER;
		}


		public String getSUFFIX()
		{
			return SUFFIX;
		}


		public void setSUFFIX(String sUFFIX)
		{
			SUFFIX = sUFFIX;
		}


		public String getADDCO()
		{
			return ADDCO;
		}


		public void setADDCO(String aDDCO)
		{
			ADDCO = aDDCO;
		}


		public String getADDCOLLALL()
		{
			return ADDCOLLALL;
		}


		public void setADDCOLLALL(String aDDCOLLALL)
		{
			ADDCOLLALL = aDDCOLLALL;
		}


		public String getTYPE()
		{
			return TYPE;
		}


		public void setTYPE(String tYPE)
		{
			TYPE = tYPE;
		}


		public String getTYPE_OF()
		{
			return TYPE_OF;
		}


		public void setTYPE_OF(String tYPE_OF)
		{
			TYPE_OF = tYPE_OF;
		}


		public String getTYPEURL()
		{
			return TYPEURL;
		}


		public void setTYPEURL(String tYPEURL)
		{
			TYPEURL = tYPEURL;
		}


		public String getDAY()
		{
			return DAY;
		}


		public void setDAY(String dAY)
		{
			DAY = dAY;
		}


		public String getMONTH()
		{
			return MONTH;
		}


		public void setMONTH(String mONTH)
		{
			MONTH = mONTH;
		}


		public String getYEAR()
		{
			return YEAR;
		}


		public void setYEAR(String yEAR)
		{
			YEAR = yEAR;
		}


		public String getDATERES()
		{
			return DATERES;
		}


		public void setDATERES(String dATERES)
		{
			DATERES = dATERES;
		}


		public String getFAMCLASS()
		{
			return FAMCLASS;
		}


		public void setFAMCLASS(String fAMCLASS)
		{
			FAMCLASS = fAMCLASS;
		}


		public String getORDER()
		{
			return ORDER;
		}


		public void setORDER(String oRDER)
		{
			ORDER = oRDER;
		}


		public String getFAMILY()
		{
			return FAMILY;
		}


		public void setFAMILY(String fAMILY)
		{
			FAMILY = fAMILY;
		}


		public String getCF()
		{
			return CF;
		}


		public void setCF(String cF)
		{
			CF = cF;
		}


		public String getTAXSTAT()
		{
			return TAXSTAT;
		}


		public void setTAXSTAT(String tAXSTAT)
		{
			TAXSTAT = tAXSTAT;
		}


		public String getSPECIES()
		{
			return SPECIES;
		}


		public void setSPECIES(String sPECIES)
		{
			SPECIES = sPECIES;
		}


		public String getDETSTATUS()
		{
			return DETSTATUS;
		}


		public void setDETSTATUS(String dETSTATUS)
		{
			DETSTATUS = dETSTATUS;
		}


		public String getDETBY()
		{
			return DETBY;
		}


		public void setDETBY(String dETBY)
		{
			DETBY = dETBY;
		}


		public String getDAYIDENT()
		{
			return DAYIDENT;
		}


		public void setDAYIDENT(String dAYIDENT)
		{
			DAYIDENT = dAYIDENT;
		}


		public String getMONTHIDENT()
		{
			return MONTHIDENT;
		}


		public void setMONTHIDENT(String mONTHIDENT)
		{
			MONTHIDENT = mONTHIDENT;
		}


		public String getYEARIDENT()
		{
			return YEARIDENT;
		}


		public void setYEARIDENT(String yEARIDENT)
		{
			YEARIDENT = yEARIDENT;
		}


		public String getDETDATE()
		{
			return DETDATE;
		}


		public void setDETDATE(String dETDATE)
		{
			DETDATE = dETDATE;
		}


		public String getDETHISTORY()
		{
			return DETHISTORY;
		}


		public void setDETHISTORY(String dETHISTORY)
		{
			DETHISTORY = dETHISTORY;
		}


		public String getDETNOTES()
		{
			return DETNOTES;
		}


		public void setDETNOTES(String dETNOTES)
		{
			DETNOTES = dETNOTES;
		}


		public String getCURATENOTE()
		{
			return CURATENOTE;
		}


		public void setCURATENOTE(String cURATENOTE)
		{
			CURATENOTE = cURATENOTE;
		}


		public String getORIGINSTAT()
		{
			return ORIGINSTAT;
		}


		public void setORIGINSTAT(String oRIGINSTAT)
		{
			ORIGINSTAT = oRIGINSTAT;
		}


		public String getORIGINID()
		{
			return ORIGINID;
		}


		public void setORIGINID(String oRIGINID)
		{
			ORIGINID = oRIGINID;
		}


		public String getCONTINENT()
		{
			return CONTINENT;
		}


		public void setCONTINENT(String cONTINENT)
		{
			CONTINENT = cONTINENT;
		}


		public String getREGION()
		{
			return REGION;
		}


		public void setREGION(String rEGION)
		{
			REGION = rEGION;
		}


		public String getCOUNTRY()
		{
			return COUNTRY;
		}


		public void setCOUNTRY(String cOUNTRY)
		{
			COUNTRY = cOUNTRY;
		}


		public String getMAJORAREA()
		{
			return MAJORAREA;
		}


		public void setMAJORAREA(String mAJORAREA)
		{
			MAJORAREA = mAJORAREA;
		}


		public String getMINORAREA()
		{
			return MINORAREA;
		}


		public void setMINORAREA(String mINORAREA)
		{
			MINORAREA = mINORAREA;
		}


		public String getLOCPREFIX()
		{
			return LOCPREFIX;
		}


		public void setLOCPREFIX(String lOCPREFIX)
		{
			LOCPREFIX = lOCPREFIX;
		}


		public String getGAZETTEER()
		{
			return GAZETTEER;
		}


		public void setGAZETTEER(String gAZETTEER)
		{
			GAZETTEER = gAZETTEER;
		}


		public String getLOCNOTES()
		{
			return LOCNOTES;
		}


		public void setLOCNOTES(String lOCNOTES)
		{
			LOCNOTES = lOCNOTES;
		}


		public String getHABITATTXT()
		{
			return HABITATTXT;
		}


		public void setHABITATTXT(String hABITATTXT)
		{
			HABITATTXT = hABITATTXT;
		}


		public String getNOTE()
		{
			return NOTE;
		}


		public void setNOTE(String nOTE)
		{
			NOTE = nOTE;
		}


		public String getCULTNOTES()
		{
			return CULTNOTES;
		}


		public void setCULTNOTES(String cULTNOTES)
		{
			CULTNOTES = cULTNOTES;
		}


		public String getLATITUDE()
		{
			return LATITUDE;
		}


		public void setLATITUDE(String lATITUDE)
		{
			LATITUDE = lATITUDE;
		}


		public String getNS()
		{
			return NS;
		}


		public void setNS(String nS)
		{
			NS = nS;
		}


		public String getLONGITUDE()
		{
			return LONGITUDE;
		}


		public void setLONGITUDE(String lONGITUDE)
		{
			LONGITUDE = lONGITUDE;
		}


		public String getEW()
		{
			return EW;
		}


		public void setEW(String eW)
		{
			EW = eW;
		}


		public String getLLUNIT()
		{
			return LLUNIT;
		}


		public void setLLUNIT(String lLUNIT)
		{
			LLUNIT = lLUNIT;
		}


		public String getLLRES()
		{
			return LLRES;
		}


		public void setLLRES(String lLRES)
		{
			LLRES = lLRES;
		}


		public String getLLORIG()
		{
			return LLORIG;
		}


		public void setLLORIG(String lLORIG)
		{
			LLORIG = lLORIG;
		}


		public String getLATLONG()
		{
			return LATLONG;
		}


		public void setLATLONG(String lATLONG)
		{
			LATLONG = lATLONG;
		}


		public String getLATDEC()
		{
			return LATDEC;
		}


		public void setLATDEC(String lATDEC)
		{
			LATDEC = lATDEC;
		}


		public String getLONGDEC()
		{
			return LONGDEC;
		}


		public void setLONGDEC(String lONGDEC)
		{
			LONGDEC = lONGDEC;
		}


		public String getDEGSQ()
		{
			return DEGSQ;
		}


		public void setDEGSQ(String dEGSQ)
		{
			DEGSQ = dEGSQ;
		}


		public String getMINEL()
		{
			return MINEL;
		}


		public void setMINEL(String mINEL)
		{
			MINEL = mINEL;
		}


		public String getMAXELEV()
		{
			return MAXELEV;
		}


		public void setMAXELEV(String mAXELEV)
		{
			MAXELEV = mAXELEV;
		}


		public String getALTRES()
		{
			return ALTRES;
		}


		public void setALTRES(String aLTRES)
		{
			ALTRES = aLTRES;
		}


		public String getALTTEXT()
		{
			return ALTTEXT;
		}


		public void setALTTEXT(String aLTTEXT)
		{
			ALTTEXT = aLTTEXT;
		}


		public String getALTRANGE()
		{
			return ALTRANGE;
		}


		public void setALTRANGE(String aLTRANGE)
		{
			ALTRANGE = aLTRANGE;
		}


		public String getGEODATA()
		{
			return GEODATA;
		}


		public void setGEODATA(String gEODATA)
		{
			GEODATA = gEODATA;
		}


		public String getPLANTDESC()
		{
			return PLANTDESC;
		}


		public void setPLANTDESC(String pLANTDESC)
		{
			PLANTDESC = pLANTDESC;
		}


		public String getNOTES()
		{
			return NOTES;
		}


		public void setNOTES(String nOTES)
		{
			NOTES = nOTES;
		}


		public String getVERNACULAR()
		{
			return VERNACULAR;
		}


		public void setVERNACULAR(String vERNACULAR)
		{
			VERNACULAR = vERNACULAR;
		}


		public String getLANGUAGE()
		{
			return LANGUAGE;
		}


		public void setLANGUAGE(String lANGUAGE)
		{
			LANGUAGE = lANGUAGE;
		}


		public String getGENUS()
		{
			return GENUS;
		}


		public void setGENUS(String gENUS)
		{
			GENUS = gENUS;
		}


		public String getSP1()
		{
			return SP1;
		}


		public void setSP1(String sP1)
		{
			SP1 = sP1;
		}


		public String getAUTHOR1()
		{
			return AUTHOR1;
		}


		public void setAUTHOR1(String aUTHOR1)
		{
			AUTHOR1 = aUTHOR1;
		}


		public String getRANK1()
		{
			return RANK1;
		}


		public void setRANK1(String rANK1)
		{
			RANK1 = rANK1;
		}


		public String getSP2()
		{
			return SP2;
		}


		public void setSP2(String sP2)
		{
			SP2 = sP2;
		}


		public String getAUTHOR2()
		{
			return AUTHOR2;
		}


		public void setAUTHOR2(String aUTHOR2)
		{
			AUTHOR2 = aUTHOR2;
		}


		public String getRANK2()
		{
			return RANK2;
		}


		public void setRANK2(String rANK2)
		{
			RANK2 = rANK2;
		}


		public String getSP3()
		{
			return SP3;
		}


		public void setSP3(String sP3)
		{
			SP3 = sP3;
		}


		public String getAUTHOR3()
		{
			return AUTHOR3;
		}


		public void setAUTHOR3(String aUTHOR3)
		{
			AUTHOR3 = aUTHOR3;
		}


		public String getUNIQUE()
		{
			return UNIQUE;
		}


		public void setUNIQUE(String uNIQUE)
		{
			UNIQUE = uNIQUE;
		}


		public String getHSACCODE()
		{
			return HSACCODE;
		}


		public void setHSACCODE(String hSACCODE)
		{
			HSACCODE = hSACCODE;
		}


		public String getGAZCODE()
		{
			return GAZCODE;
		}


		public void setGAZCODE(String gAZCODE)
		{
			GAZCODE = gAZCODE;
		}


		public String getHBCODE()
		{
			return HBCODE;
		}


		public void setHBCODE(String hBCODE)
		{
			HBCODE = hBCODE;
		}


		public String getHSTYPE()
		{
			return HSTYPE;
		}


		public void setHSTYPE(String hSTYPE)
		{
			HSTYPE = hSTYPE;
		}


		public String getSPTYPE()
		{
			return SPTYPE;
		}


		public void setSPTYPE(String sPTYPE)
		{
			SPTYPE = sPTYPE;
		}


		public String getSPNUMBER()
		{
			return SPNUMBER;
		}


		public void setSPNUMBER(String sPNUMBER)
		{
			SPNUMBER = sPNUMBER;
		}


		public String getSPCODETYPE()
		{
			return SPCODETYPE;
		}


		public void setSPCODETYPE(String sPCODETYPE)
		{
			SPCODETYPE = sPCODETYPE;
		}


		public String getCSCODE()
		{
			return CSCODE;
		}


		public void setCSCODE(String cSCODE)
		{
			CSCODE = cSCODE;
		}


		public String getALTCS()
		{
			return ALTCS;
		}


		public void setALTCS(String aLTCS)
		{
			ALTCS = aLTCS;
		}


		public String getADDCSCODE()
		{
			return ADDCSCODE;
		}


		public void setADDCSCODE(String aDDCSCODE)
		{
			ADDCSCODE = aDDCSCODE;
		}


		public String getDETBYCODE()
		{
			return DETBYCODE;
		}


		public void setDETBYCODE(String dETBYCODE)
		{
			DETBYCODE = dETBYCODE;
		}


		public String getCONUMBER()
		{
			return CONUMBER;
		}


		public void setCONUMBER(String cONUMBER)
		{
			CONUMBER = cONUMBER;
		}


		public String getCCID()
		{
			return CCID;
		}


		public void setCCID(String cCID)
		{
			CCID = cCID;
		}


		public String getENTRYDATE()
		{
			return ENTRYDATE;
		}


		public void setENTRYDATE(String eNTRYDATE)
		{
			ENTRYDATE = eNTRYDATE;
		}


		public String getWHO()
		{
			return WHO;
		}


		public void setWHO(String wHO)
		{
			WHO = wHO;
		}


		public String getNOTONLINE()
		{
			return NOTONLINE;
		}


		public void setNOTONLINE(String nOTONLINE)
		{
			NOTONLINE = nOTONLINE;
		}


		public String getDATELASTM()
		{
			return DATELASTM;
		}


		public void setDATELASTM(String dATELASTM)
		{
			DATELASTM = dATELASTM;
		}


		public String getIMAGELIST()
		{
			return IMAGELIST;
		}


		public void setIMAGELIST(String iMAGELIST)
		{
			IMAGELIST = iMAGELIST;
		}


		@Override
		public String toString()
		{
			return "\n Brahms:" + "\n Tag=" + getTAG() + "\n Del:" + getDEL() + "\n Herbarium:" + getHERBARIUM() + "\n Category:" + getCATEGORY()
					+ "\n Specid:" + getSPECID() + "\n Brahms:" + getBRAHMS() + "\n Accession:" + getACCESSION() + "\n Barcode:" + getBARCODE()
					+ "\n OldBarcode:" + getOLDBARCODE() + "\n Phenology:" + getPHENOLOGY() + "\n Collector:" + getCOLLECTOR() + "\n Prefix:"
					+ getPREFIX() + "\n Number:" + getNUMBER() + "\n Suffix:" + getSUFFIX() + "\n Addcoll:" + getADDCO() + "\n AddCollAll:"
					+ getADDCOLLALL() + "\n Type:" + getTYPE() + "\n TypeOf:" + getTYPE_OF() + "\n TypeUrl:" + getTYPEURL() + "\n Day:" + getDAY()
					+ "\n Month:" + getMONTH() + "\n Year:" + getYEAR() + "\n DateRes:" + getDATERES() + "\n FamClass:" + getFAMCLASS() + "\n Order:"
					+ getORDER() + "\n Family:" + getFAMILY() + "\n CF:" + getCF() + "\n TaxStat:" + getTAXSTAT() + "\n Species:" + getSPECIES()
					+ "\n DetStatus:" + getDETSTATUS() + "\n DetBy:" + getDETBY() + "\n DayIndent:" + getDAYIDENT() + "\n MonthIdent:"
					+ getMONTHIDENT() + "\n YearIdent:" + getYEARIDENT() + "\n DetDate:" + getDETDATE() + "\n DetHistory:" + getDETHISTORY()
					+ "\n DetNotes:" + getDETNOTES() + "\n CurateNote:" + getCURATENOTE() + "\n OriginStat:" + getORIGINSTAT() + "\n OriginID:"
					+ getORIGINID() + "\n Continent:" + getCONTINENT() + "\n Region:" + getREGION() + "\n Country:" + getCOUNTRY() + "\n MajorArea:"
					+ getMAJORAREA() + "\n NinorArea:" + getMINORAREA() + "\n LocPrefix:" + getLOCPREFIX() + "\n Gazetteer:" + getGAZETTEER()
					+ "\n LocNotes:" + getLOCNOTES() + "\n HabitatTxt:" + getHABITATTXT() + "\n Note:" + getNOTE() + "\n CulNotes:" + getCULTNOTES()
					+ "\n Latitude:" + getLATITUDE() + "\n NS:" + getNS() + "\n LongTitude:" + getLONGITUDE() + "\n EW:" + getEW() + "\n LLUnit:"
					+ getLLUNIT() + "\n LLRes:" + getLLRES() + "\n LLOrig:" + getLLORIG() + "\n LatLong:" + getLATLONG() + "\n LatDec:" + getLATDEC()
					+ "\n LongDec:" + getLONGDEC() + "\n DegSQ:" + getDEGSQ() + "\n Minelev:" + getMINEL() + "\n MaxeLev:" + getMAXELEV()
					+ "\n AltRes:" + getALTRES() + "\n AltText:" + getALTTEXT() + "\n AltRange:" + getALTRANGE() + "\n GeoData:" + getGEODATA()
					+ "\n PlantDesc:" + getPLANTDESC() + "\n Notes:" + getNOTES() + "\n Vernacular:" + getVERNACULAR() + "\n Language:"
					+ getLANGUAGE() + "\n Genus:" + getGENUS() + "\n SP1:" + getSP1() + "\n Author1:" + getAUTHOR1() + "\n Rank1:" + getRANK1()
					+ "\n SP2:" + getSP2() + "\n Author2:" + getAUTHOR2() + "\n Rank2:" + getRANK2() + "\n SP3:" + getSP3() + "\n Author3:"
					+ getAUTHOR3() + "\n Unique:" + getUNIQUE() + "\n HsacCode:" + getHSACCODE() + "\n GazCode:" + getGAZCODE() + "\n HbCode:"
					+ getHBCODE() + "\n HsType:" + getHSTYPE() + "\n SpType:" + getSPTYPE() + "\n SpNumber:" + getSPNUMBER() + "\n SpCodeType:"
					+ getSPCODETYPE() + "\n CsCode:" + getCSCODE() + "\n AltCs:" + getALTCS() + "\n AddCsCode:" + getADDCSCODE() + "\n DetByCode:"
					+ getDETBYCODE() + "\n CoNumber:" + getCONUMBER() + "\n CCID:" + getCCID() + "\n EntryDate:" + getENTRYDATE() + "\n WHO:"
					+ getWHO() + "\n NotOnline:" + getNOTONLINE() + "\n DateLastM:" + getDATELASTM() + "\n ImageList:" + getIMAGELIST()
					+ "\n ................................................." + "\n file " + FileCSVnm + " [record ]" + getSPECID();
		}

		private String TAG;
		private String DEL;
		private String HERBARIUM;
		private String CATEGORY;
		private String SPECID;
		private String BRAHMS;
		private String ACCESSION;
		private String BARCODE;
		private String OLDBARCODE;
		private String PHENOLOGY;
		private String COLLECTOR;
		private String PREFIX;
		private String NUMBER;
		private String SUFFIX;
		private String ADDCO;
		private String ADDCOLLALL;
		private String TYPE;
		private String TYPE_OF;
		private String TYPEURL;
		private String DAY;
		private String MONTH;
		private String YEAR;
		private String DATERES;
		private String FAMCLASS;
		private String ORDER;
		private String FAMILY;
		private String CF;
		private String TAXSTAT;
		private String SPECIES;
		private String DETSTATUS;
		private String DETBY;
		private String DAYIDENT;
		private String MONTHIDENT;
		private String YEARIDENT;
		private String DETDATE;
		private String DETHISTORY;
		private String DETNOTES;
		private String CURATENOTE;
		private String ORIGINSTAT;
		private String ORIGINID;
		private String CONTINENT;
		private String REGION;
		private String COUNTRY;
		private String MAJORAREA;
		private String MINORAREA;
		private String LOCPREFIX;
		private String GAZETTEER;
		private String LOCNOTES;
		private String HABITATTXT;
		private String NOTE;
		private String CULTNOTES;
		private String LATITUDE;
		private String NS;
		private String LONGITUDE;
		private String EW;
		private String LLUNIT;
		private String LLRES;
		private String LLORIG;
		private String LATLONG;
		private String LATDEC;
		private String LONGDEC;
		private String DEGSQ;
		private String MINEL;
		private String MAXELEV;
		private String ALTRES;
		private String ALTTEXT;
		private String ALTRANGE;
		private String GEODATA;
		private String PLANTDESC;
		private String NOTES;
		private String VERNACULAR;
		private String LANGUAGE;
		private String GENUS;
		private String SP1;
		private String AUTHOR1;
		private String RANK1;
		private String SP2;
		private String AUTHOR2;
		private String RANK2;
		private String SP3;
		private String AUTHOR3;
		private String UNIQUE;
		private String HSACCODE;
		private String GAZCODE;
		private String HBCODE;
		private String HSTYPE;
		private String SPTYPE;
		private String SPNUMBER;
		private String SPCODETYPE;
		private String CSCODE;
		private String ALTCS;
		private String ADDCSCODE;
		private String DETBYCODE;
		private String CONUMBER;
		private String CCID;
		private String ENTRYDATE;
		private String WHO;
		private String NOTONLINE;
		private String DATELASTM;
		private String IMAGELIST;

	}
}
