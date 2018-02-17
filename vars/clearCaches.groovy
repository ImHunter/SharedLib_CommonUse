import org.apache.ivy.util.FileUtil
import org.apache.tools.ant.util.FileUtils

import java.nio.file.Paths
import Energos.Jenkins.Common.Dir

def call(){

    def vars = System.getenv()
    String[] dirs = [vars.get('APPDATA'), vars.get('LOCALAPPDATA')]
    def cachesPath
    echo "${dirs}"

    dirs.each {String envDir ->
        cachesPath = Paths.get(envDir, '1C', '1cv8').toString()
//        echo cachesPath
        def parentDir = new Dir(cachesPath)
        echo parentDir.toString()
//        parentDir.findDirs('????????-????-????-????-????????????').each { def cache ->
        parentDir.findDirs('*-*-*-*-*').each { def cache ->
            echo "deletion: ${cache}"
            FileUtil.forceDelete(cache)
        }
    }

}
