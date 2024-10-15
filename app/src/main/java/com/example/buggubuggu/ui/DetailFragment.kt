package com.example.buggubuggu.ui

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.buggubuggu.R
import com.example.buggubuggu.databinding.FragmentDetailBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapGravity
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapType
import com.kakao.vectormap.MapView
import com.kakao.vectormap.MapViewInfo
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles


class DetailFragment : Fragment() {
    private var _binding:FragmentDetailBinding?=null
    private val binding get()=_binding!!
    private var mapView: MapView? = null

    private var x:Double = 0.0
    private var y:Double = 0.0


    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        if (it) {
            Log.d("DetailFragment", "Permission granted")
            getLocationAndNavigate { bundle ->
                Log.d("DetailFragment", "Navigating to findFragment with bundle: $bundle")
                findNavController().navigate(R.id.action_detailFragment_to_findFragment, bundle)
            }
        } else {
            // 권한이 거부되었을 때 처리
            Log.d("FindFragment", "Location permission denied")
        }
    }



    private val model:DetailViewModel by viewModels()

    companion object {
        fun newInstance() = DetailFragment()
    }

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val keyword = arguments?.getString("itemName").toString()

        binding.textViewName.text = arguments?.getString("itemName")
        binding.textViewSectors.text = arguments?.getString("itemSector")
        binding.textViewDate.text = arguments?.getString("itemDate")
        binding.textViewAddress.text = arguments?.getString("itemAddress")


        binding.buttonFind.setOnClickListener{

//            findNavController().navigate(R.id.action_detailFragment_to_findFragment)
            checkPermissionAndNavigate()
        }

        val result = keyword.substringBefore('(')

        model.getSearch(result) // 주소 검색


        mapView = binding.mapView

        mapView?.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출됨
                // 필요한 정리 작업을 이곳에 작성하세요.
                // 예시: 지도 리소스 정리
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
                // 오류 처리 로직을 이곳에 작성하세요.
                // 예시: 로그 출력
                Log.e("IntroFragment", "Map error occurred", error)
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 인증 후 API가 정상적으로 실행될 때 호출됨
                // 지도가 준비되었을 때 실행할 로직을 이곳에 작성하세요.
                // 예시: 지도 초기화
                kakaoMap.setOnMapViewInfoChangeListener(object : KakaoMap.OnMapViewInfoChangeListener {
                    override fun onMapViewInfoChanged(mapViewInfo: MapViewInfo) {
                        // MapViewInfo 변경 성공 시 호출
                    }

                    override fun onMapViewInfoChangeFailed() {
                        // MapViewInfo 변경 실패 시 호출
                    }
                })

                kakaoMap.changeMapViewInfo(MapViewInfo.from("openmap", MapType.NORMAL))

                // 검색 결과가 존재하면 지도를 해당 좌표로 이동
                model.results.observe(viewLifecycleOwner) { result ->
                    if (result.documents.isNotEmpty()) {
                        x = result.documents[0].x.toDouble()
                        y = result.documents[0].y.toDouble()


                        // 마커
                        // 1. LabelStyles 생성하기 - Icon 이미지 하나만 있는 스타일
                        val styles = kakaoMap.labelManager
                            ?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.marker_spot).setZoomLevel(0)))

                        // 2. LabelOptions 생성하기
                        val options = LabelOptions.from(LatLng.from(y,x))
                            .setStyles(styles)
                            .setTexts("제발 나와라")

                        // 3. LabelLayer 가져오기 (또는 커스텀 Layer 생성)
                        val layer = kakaoMap.labelManager?.layer

                        // 4. LabelLayer 에 LabelOptions 을 넣어 Label 생성하기
                        Log.d("DetailFragment", "Adding label at coordinates: ($x, $y)")
                        val label = layer!!.addLabel(options)
                        Log.d("DetailFragment", "Label added: ${label.isShow}")


                        // 지도 이동 코드 추가
                        val cameraUpdate = CameraUpdateFactory.newCenterPosition(
                            LatLng.from(y, x)
                        )
                        kakaoMap.moveCamera(cameraUpdate)


                        // 나침반
                        val compass = kakaoMap.compass

                        compass?.setPosition(MapGravity.TOP or MapGravity.LEFT, 0f, 0f)

                        compass?.show()

                        // 스케일 바
                        val scaleBar = kakaoMap.scaleBar;

                        scaleBar?.show();
                    }
                }

            }

        })



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        mapView?.resume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.pause()
    }


    private fun checkPermissionAndNavigate() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getLocationAndNavigate { bundle ->
                findNavController().navigate(R.id.action_detailFragment_to_findFragment, bundle)
            }
        }
    }

    private fun getLocationAndNavigate(callback: (Bundle) -> Unit) {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            val locationManager = requireContext().getSystemService(LocationManager::class.java)
            val provider = LocationManager.GPS_PROVIDER
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                val bundle = createBundleWithLocation(location)
                callback(bundle)
            } else {
                locationManager.requestLocationUpdates(provider, 3000L, 100f) { result ->
                    val bundle = createBundleWithLocation(result)
                    callback(bundle)
                }
            }
        }
    }

    private fun createBundleWithLocation(location: Location): Bundle {
        val latitude = location.latitude
        val longitude = location.longitude
        Log.d("DetailFragment", "Location: $latitude, $longitude")
        Log.d("DetailFragment", "itemPosition: $y, $x")

        return Bundle().apply {
            putDouble("latitude", latitude)
            putDouble("longitude", longitude)
            putDouble("itemY", y)
            putDouble("itemX", x)
            putString("itemName", arguments?.getString("itemName"))
        }
    }
}