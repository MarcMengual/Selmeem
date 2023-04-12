package cat.copernic.roomdecision.selmeem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.roomdecision.selmeem.RVPublicacionsInici.Item
import cat.copernic.roomdecision.selmeem.RVPublicacionsInici.MyAdapter
import cat.copernic.roomdecision.selmeem.databinding.FragmentPantallaInicialBinding
import java.util.*

class Pantalla_inicial : Fragment() {

    private var _binding: FragmentPantallaInicialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPantallaInicialBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = ArrayList<Item>()
        items.add(Item("Hola", "jaja", "", 9))

        binding.RVPublicacions.layoutManager = LinearLayoutManager(requireContext())
        binding.RVPublicacions.adapter = MyAdapter(requireContext(), items)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
