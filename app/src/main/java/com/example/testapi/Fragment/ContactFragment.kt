package com.example.testapi.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.example.testapi.R
import com.example.testapi.databinding.FragmentContactBinding
import com.example.testapi.databinding.FragmentProfileBinding
import java.util.Locale


class ContactFragment : Fragment() {
    private var _binding : FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        var phoneNumber = binding.contactPhone
        var email = binding.contactEmail
        var location = binding.contactLocation
        var contact_back = binding.contactBack

        phoneNumber.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:0898604143")
            startActivity(intent)
        }
        email.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:hulosportshop@gmail.com")
            startActivity(intent)
        }
        location.setOnClickListener{
            var uri = String.format(Locale.ENGLISH,"geo:0,0?q=%f,%f(%s)", 15.976375, 108.253989, "Trung tâm điều hành")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }
        return binding.root
    }
}