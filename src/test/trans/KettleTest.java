package test.trans;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lwj
 */
public class KettleTest {
    private static final Logger log = LoggerFactory.getLogger(KettleTest.class);

    private static KettleDatabaseRepository repository;
    private static RepositoryDirectoryInterface directory;

    public static void main(String... args){
        initRepo();
        try {
            runTrans();
        }catch (Exception e){
            log.error("run trans error", e);
        }
    }

    public static void initRepo(){
        try {
            KettleEnvironment.init();
            repository = new KettleDatabaseRepository();
            DatabaseMeta databaseMeta = new DatabaseMeta("kettle_repo", "MySql", "Native", "192.168.0.101",
                    "kettle", "3306",
                    "manager", "manager");//资源库数据库地址，我这里采用oracle数据库
            KettleDatabaseRepositoryMeta kettleDatabaseMeta = new KettleDatabaseRepositoryMeta("kettle_repo", "kettle_repo",
                    "Transformation description", databaseMeta);
            repository.init(kettleDatabaseMeta);
            repository.connect("admin", "admin");//资源库用户名和密码
            directory = repository.loadRepositoryDirectoryTree();

        } catch (KettleException e) {
            log.error("init kettle error :", e);
        }
    }

    public static void runTrans() throws KettleException {
        //根据变量查找到模型所在的目录对象,此步骤很重要。
        RepositoryDirectoryInterface directory = repository.findDirectory("/");
        //创建ktr元对象
        TransMeta transformationMeta = repository.loadTransformation("marriage_trans", directory, null, true, null);

        transformationMeta.setParameterValue("original_database_password", "gerakan");

        //创建ktr
        Trans trans = new Trans(transformationMeta);

        //执行ktr
        String[] params = {"original_database_port"};
        trans.execute( params);
        //等待执行完毕
        trans.waitUntilFinished();
        if (trans.getErrors() > 0) {
            log.info("trans executed failed");
        } else {
            log.info("trans executed OK");
        }
    }

    public static void runJob() throws KettleException {
        JobMeta jobMeta = ((Repository) repository).loadJob("job", directory, null, null);
        Job job = new Job(repository, jobMeta);
        job.setVariable("id", String.valueOf(1));
        job.start();
        job.waitUntilFinished();
        if (job.getErrors() > 0) {
            log.info("trans executed failed");
        } else {
            log.info("trans executed OK");
        }
    }
}
