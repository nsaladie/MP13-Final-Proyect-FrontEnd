interface RemoteApiMessageAuxiliary {
        data class Success(val message: Boolean) : RemoteApiMessageAuxiliary
        object Loading : RemoteApiMessageAuxiliary
        object Error : RemoteApiMessageAuxiliary
}