def call(String jobName, def buildNumber = null){
    
    String logContent;
    
    // def core = Jenkins.getInstance()
    def core = Jenkins.getInstanceOrNull();
    def job = core.getItemByFullName(jobName);
    def lastBuildNum = buildNumber==null ? job.getLastBuild().getNumber(): buildNumber;

    def build = job.getBuildByNumber(lastBuildNum);
    def logFilePath = build.getLogFile().getPath();

    File logFile = new java.io.File(logFilePath); 
    logContent = logFile.text;
    println(logContent);

    logContent;

}