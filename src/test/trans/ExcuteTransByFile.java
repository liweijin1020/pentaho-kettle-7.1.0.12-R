package test.trans;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

public class ExcuteTransByFile {
    public static void main(String[] args) {
        String filename = "E:\\DevelopmentCourse\\ETL\\kettle\\testdata\\tableinput.ktr";// ktr文件名
        try {
            KettleEnvironment.init();
            TransMeta transMeta = new TransMeta(filename);
            Trans trans = new Trans(transMeta);
            trans.execute(null); // You can pass arguments instead of null.
            trans.waitUntilFinished();// 等transformation执行结束
            if (trans.getErrors() > 0) {
                throw new RuntimeException("There were errors during transformation execution.");
            }
        } catch (KettleException e) {
            // TODO Put your exception-handling code here.
            // System.out.println(filename);
            System.out.println(e);
        }
    }

}
