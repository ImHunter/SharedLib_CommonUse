package Energos.Jenkins.Common

import Energos.Jenkins.Common.Dir

def call(String path){
    new Energos.Jenkins.Common.Dir(path)
}