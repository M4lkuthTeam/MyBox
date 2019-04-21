package mara.mybox.data;

import java.io.File;
import java.util.Date;

/**
 * @Author Mara
 * @CreateDate 2019-4-11 10:54:30
 * @Version 1.0
 * @Description
 * @License Apache License Version 2.0
 */
public class ProcessParameters {

    public File sourceFile;
    public int startIndex, currentIndex, currentTotalHandled;
    public String status, targetPath, targetPrefix, targetRootPath, finalTargetName;
    public Date startTime, endTime;
    public boolean createSubDir, isBatch;
    public boolean fill, aSize, aColor, aCompression, aQuality, aDensity;
    public int fromPage, toPage, startPage, acumFrom, acumStart, acumDigit;
    public String password;
    public int currentPage, currentNameNumber;

}
