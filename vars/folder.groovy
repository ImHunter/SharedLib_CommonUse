package Energos.Jenkins.Common

import Energos.Jenkins.Common.Folder

def call(String path){
    new Energos.Jenkins.Common.Folder(path)
}