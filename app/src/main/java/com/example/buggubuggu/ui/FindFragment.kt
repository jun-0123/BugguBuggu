package com.example.buggubuggu.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.buggubuggu.R
import com.example.buggubuggu.databinding.FragmentFindBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapGravity
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapType
import com.kakao.vectormap.MapView
import com.kakao.vectormap.MapViewInfo
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLinePattern
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet


class FindFragment : Fragment() {
    private var _binding: FragmentFindBinding?=null
    private val binding get()=_binding!!
    private var mapView: MapView? = null

    private var latitude:Double = 0.0
    private var longitude:Double = 0.0
    private var itemX:Double = 0.0
    private var itemY:Double = 0.0


    companion object {
        fun newInstance() = FindFragment()
    }

    private val viewModel: FindViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            latitude = it.getDouble("latitude")
            longitude = it.getDouble("longitude")
            itemX = it.getDouble("itemX")
            itemY = it.getDouble("itemY")
            // latitude와 longitude를 사용한 추가 코드
        }


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

                // 마커
                // 1. LabelStyles 생성하기 - Icon 이미지 하나만 있는 스타일
                val styles = kakaoMap.labelManager
                    ?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.marker_spot).setZoomLevel(0)))

                // 2. LabelOptions 생성하기
                val options = LabelOptions.from(LatLng.from(latitude,longitude))
                    .setStyles(styles)

                Log.d("FindFragment", " Item position Adding label at coordinates: ($itemY,$itemX)")

                // 3. LabelLayer 가져오기 (또는 커스텀 Layer 생성)
                val layer = kakaoMap.labelManager?.layer


                // 4. LabelLayer 에 LabelOptions 을 넣어 Label 생성하기
                Log.d("FindFragment", "position Adding label at coordinates: ($latitude,$longitude)")
                val label = layer!!.addLabel(options)

                // 1-2.가게 위치
                val styles2 = kakaoMap.labelManager
                    ?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.marker_spot).setZoomLevel(0)))

                // 2-2. 가게 위치LabelOptions 생성하기
                val options2 = LabelOptions.from(LatLng.from(itemY,itemX))
                    .setStyles(styles)


                // 3-2. LabelLayer 가져오기 (또는 커스텀 Layer 생성)
                val layer2 = kakaoMap.labelManager?.layer


                // 4-2. LabelLayer 에 LabelOptions 을 넣어 Label 생성하기
                Log.d("FindFragment", "position Adding label at coordinates: ($latitude,$longitude)")

                val label2 = layer2!!.addLabel(options2)

                Log.d("FindFragment", "Label added: ${label.isShow}")
                Log.d("FindFragment", "Label added: ${label2.isShow}")



                val trackingManager = kakaoMap.trackingManager
                trackingManager!!.startTracking(label)

                // Label 의 회전 방향에 따라 같이 회전하도록 설정하는 옵션
                trackingManager.setTrackingRotation(true)



                val routeLineLayer = kakaoMap.routeLineManager?.layer

                // 2. RouteLineStylesSet 생성하기
                val stylesSet = RouteLineStylesSet.from(
                    "blueStyles",
                    RouteLineStyles.from(RouteLineStyle.from(16f, Color.BLUE)
                        .setPattern(RouteLinePattern.from(R.drawable.route_pattern_arrow,12f)))
                )

                // 3. RouteLineSegment 생성하기 - 세그먼트에 스타일 설정을 생략하면, RouteLineStylesSet 의 index 0 번째에 해당되는 스타일로 설정된다.
                // 3-1. index 를 통해 RouteLineStylesSet 에 있는 styles 를 가져온다.

                val segment = RouteLineSegment.from(
                    listOf(
                        LatLng.from(latitude, longitude),
                        LatLng.from(itemY, itemX)
                    )
                )
                    .setStyles(stylesSet.getStyles(0))


                // 4. RouteLineStylesSet 을 추가하고 RouteLineOptions 생성하기
                val routeOptions = RouteLineOptions.from(segment)
                    .setStylesSet(stylesSet)


                // 5. RouteLineLayer 에 추가하여 RouteLine 생성하기
                val routeLine: RouteLine? = routeLineLayer?.addRouteLine(routeOptions)


                // 나침반
                val compass = kakaoMap.compass

                compass?.setPosition(MapGravity.TOP or MapGravity.LEFT, 0f, 0f)

                compass?.show()

                // 스케일 바
                val scaleBar = kakaoMap.scaleBar;

                scaleBar?.show();


            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentFindBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}