/*
Copyright 2017, Fr Capgemini, R. Mauget/JF. Hubler

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package nato.ivct.etc.fr.tc_hla_declaration;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
import de.fraunhofer.iosb.tc_lib.IVCT_LoggingFederateAmbassador;
import de.fraunhofer.iosb.tc_lib.IVCT_RTI_Factory;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcFailed;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import hla.rti1516e.FederateHandle;
import nato.ivct.etc.fr.tc_lib_hla_declaration.HLA_Declaration_BaseModel;
import nato.ivct.etc.fr.tc_lib_hla_declaration.HLA_Declaration_TcParam;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Constant;
import nato.ivct.etc.fr.fctt_common.utils.TextInternationalization;

import org.slf4j.Logger;

import java.io.File;

/**
 * @author Fr Capgemini, R. Mauget/JF. Hubler
 */
public class TC_001_Publish_Subscribe_Check extends AbstractTestCase {
    FederateHandle                              TcFederateHandle;
    private String                              TcFederateName = "IVCT_HLA_Declaration";

    // Build test case parameters to use
    static HLA_Declaration_TcParam              HlaDeclarationTcParam;

    // Get logging-IVCT-RTI using tc_param federation name, host
    private static IVCT_RTIambassador           ivct_rti;
    static HLA_Declaration_BaseModel            HlaDeclarationBaseModel;
    
    static IVCT_LoggingFederateAmbassador		ivct_LoggingFederateAmbassador;

    
    @Override
    public IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger) throws TcInconclusive {

    	try {
	    	HlaDeclarationTcParam           = new HLA_Declaration_TcParam(tcParamJson);
	    	ivct_rti                        = IVCT_RTI_Factory.getIVCT_RTI(logger);
	    	HlaDeclarationBaseModel         = new HLA_Declaration_BaseModel(logger, ivct_rti, HlaDeclarationTcParam);
	    	ivct_LoggingFederateAmbassador  = new IVCT_LoggingFederateAmbassador(HlaDeclarationBaseModel, logger);
    	}
    	catch(Exception ex) {
    		logger.error(TextInternationalization.getString("etc_fra.noInstanciation"));
    	}
    	return HlaDeclarationBaseModel;
    }

    @Override
    protected void logTestPurpose(final Logger logger) {
    	
    	// Build purpose text
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FCTT_Constant.REPORT_FILE_SEPARATOR);
        stringBuilder.append(TextInternationalization.getString("etc_fra.purpose")); stringBuilder.append("\n");
        stringBuilder.append(TextInternationalization.getString("hladeclaration.objectsPublication")); stringBuilder.append("\n");
        stringBuilder.append(TextInternationalization.getString("hladeclaration.interactionsPublication")); stringBuilder.append("\n");
        stringBuilder.append(TextInternationalization.getString("hladeclaration.FomSomComparison1")); stringBuilder.append("\n");
        stringBuilder.append(TextInternationalization.getString("hladeclaration.FomSomComparison2")); stringBuilder.append("\n");
        final String testPurpose = stringBuilder.toString();
        logger.info(testPurpose);
    }


    @Override
    protected void preambleAction(final Logger logger) throws TcInconclusive {

    	// Environment verification
//    	logger.error(String.format("*** RMA  *** : " + System.getenv("se.pitch.prti1516e.enableSmartFormatterForReporting")));
    	
    	// Load FOM/SOM files
        if (HlaDeclarationBaseModel.loadFomSomFiles() == false)
        	throw new TcInconclusive(TextInternationalization.getString("etc_fra.FomSomError"));
        
    	// Initiate rti
        TcFederateHandle = HlaDeclarationBaseModel.initiateRti(TcFederateName, ivct_LoggingFederateAmbassador);

        // Do the necessary calls to get handles and do publish and subscribe
        if (HlaDeclarationBaseModel.init(HlaDeclarationTcParam.getSutName()))
            throw new TcInconclusive(TextInternationalization.getString("etc_fra.initError"));

    	logger.info(TextInternationalization.getString("etc_fra.RtiConnected"));
    	logger.info(FCTT_Constant.REPORT_FILE_SEPARATOR);
    }


    @Override
    protected void performTest(final Logger logger) throws TcInconclusive, TcFailed {

    	// Check result directory
    	String resultFileName = HlaDeclarationTcParam.getResultDir();
        File resultFile = new File(resultFileName);
        if (!resultFile.exists())
            throw new TcInconclusive(String.format(TextInternationalization.getString("etc_fra.resultDirError"),resultFileName));
    	
        // Allow time to work and get some reflect values.
        if (HlaDeclarationBaseModel.sleepFor(logger,HlaDeclarationTcParam.getTestDuration())) {
            throw new TcInconclusive(TextInternationalization.getString("etc_fra.sleepError"));
        }
        
    	logger.info(TextInternationalization.getString("etc_fra.wakeup"));

    	// Generate result files
        if (HlaDeclarationBaseModel.validateDeclarations() == false)
        	throw new TcFailed(TextInternationalization.getString("hladeclaration.invalidDeclaration"));
    }


    @Override
    protected void postambleAction(final Logger logger) throws TcInconclusive, TcInconclusive {
        
        // Terminate rti
        HlaDeclarationBaseModel.terminateRti();
    }
}
