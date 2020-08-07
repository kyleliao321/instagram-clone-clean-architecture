package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main.compound

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.feature_profile.databinding.FragmentProfileMainUserProfileBinding
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel

class UserProfileView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        val inflater = LayoutInflater.from(context)
        val binding = FragmentProfileMainUserProfileBinding.inflate(
            inflater, this, true
        )
    }
}