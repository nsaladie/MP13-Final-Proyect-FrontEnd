import com.example.hospitalfrontend.model.AuxiliaryState

interface RemoteApiMessageAuxiliary {
        data class Success(val message: AuxiliaryState) : RemoteApiMessageAuxiliary
        object Loading : RemoteApiMessageAuxiliary
        object Error : RemoteApiMessageAuxiliary
}