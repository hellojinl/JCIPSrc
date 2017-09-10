package support.log;

import java.io.File;
import java.io.IOException;

public final class LogCreator {

    private LogCreator() {
    }

    public static File createLog(String name) throws IOException {
        File file = new File( name );
        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (isDeleted) {
                System.out.println( "删除已经存在的日志文件" );
            }
        }
        file.createNewFile();
        System.out.println( "创建日志文件：" + file.getAbsolutePath() );
        return file;
    }
}
