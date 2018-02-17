import java.nio.file.Paths
import Energos.Jenkins.Common.Dir

def call(){

    def vars = System.getenv()
    String[] dirs = [vars.get('APPDATA'), vars.get('LOCALAPPDATA')]
    def cachesPath
    echo "${dirs}"

    dirs.each {String envDir ->
        cachesPath = Paths.get(envDir, '1C', '1cv8').toString()
        echo cachesPath
        def parentDir = new Dir(cachesPath)
        echo parentDir.toString()
        parentDir.findDirs('????????-????-????-????-????????????').each { cache ->
            echo "${cache}"
        }
    }

}
