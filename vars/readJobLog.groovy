/**
 * Функция, прочитывающая лог выполнения задания Jenkins
 * @param jobName Имя задания
 * @param buildNumber Номер сборки. Если не задать, то прочитается лог самой последней сборки
 * @return Содержание лога
 */
def call(String jobName = null, def buildNumber = null){
    
    String logContent
    
    // def core = Jenkins.getInstance()
    def core = Jenkins.getInstanceOrNull()
    def job = core.getItemByFullName(jobName==null ? envVar.JOB_NAME : jobName)
    def lastBuildNum = buildNumber==null ? job.getLastBuild().getNumber(): buildNumber

    def build = job.getBuildByNumber(lastBuildNum)
    def logFilePath = build.getLogFile().getPath()

    File logFile = new java.io.File(logFilePath)
    logContent = logFile.text

    logContent

}