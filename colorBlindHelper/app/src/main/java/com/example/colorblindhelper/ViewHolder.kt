import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.colorblindhelper.R
import com.example.colorblindhelper.userModel


class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    public var tvUserName : TextView? = null
    public var imgViewProfile: ImageView? = null
    init{
        tvUserName = view.findViewById(R.id.tvUserName)
        imgViewProfile = view.findViewById(R.id.ImgViewProfile)

    }
}
