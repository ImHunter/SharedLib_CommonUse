import org.apache.ivy.util.FileUtil
import org.apache.tools.ant.util.FileUtils

import java.nio.file.Paths
import Energos.Jenkins.Common.Dir

def call(){

    def vars = System.getenv()
    String[] dirs = [vars.get('APPDATA'), vars.get('LOCALAPPDATA')]
    def cachesPath

    dirs.each {String envDir ->
        cachesPath = Paths.get(envDir, '1C', '1cv8').toString()
//        def parentDir =
        new Dir(cachesPath).findDirs('????????-????-????-????-????????????').each { def cache ->
            echo "deletion: ${cache}"
//            FileUtil.forceDelete(cache)
        }
    }

}
