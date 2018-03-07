package Energos.Jenkins.Common

import Common.Folder

def call(String path){
    new Folder(path)
}