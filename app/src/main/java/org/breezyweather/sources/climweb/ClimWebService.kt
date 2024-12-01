/**
 * This file is part of Breezy Weather.
 *
 * Breezy Weather is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, version 3 of the License.
 *
 * Breezy Weather is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Breezy Weather. If not, see <https://www.gnu.org/licenses/>.
 */

package org.breezyweather.sources.climweb

import android.content.Context
import breezyweather.domain.feature.SourceFeature
import breezyweather.domain.location.model.Location
import breezyweather.domain.weather.wrappers.SecondaryWeatherWrapper
import io.reactivex.rxjava3.core.Observable
import org.breezyweather.R
import org.breezyweather.common.preference.EditTextPreference
import org.breezyweather.common.preference.Preference
import org.breezyweather.common.source.ConfigurableSource
import org.breezyweather.common.source.HttpSource
import org.breezyweather.common.source.LocationParametersSource
import org.breezyweather.common.source.SecondaryWeatherSource
import org.breezyweather.settings.SourceConfigStore
import org.breezyweather.sources.climweb.json.ClimWebAlertsResult
import org.breezyweather.sources.climweb.json.ClimWebNormals
import retrofit2.Retrofit

/**
 * Open-source system used by many African countries
 * https://github.com/wmo-raf/climweb
 *
 * Is an abstract class that must be implemented by each national source
 */
abstract class ClimWebService : HttpSource(), SecondaryWeatherSource, ConfigurableSource, LocationParametersSource {

    protected abstract val context: Context
    protected abstract val jsonClient: Retrofit.Builder

    /**
     * E.g. https://www.weatherzw.org.zw/
     */
    protected abstract val baseUrl: String

    /**
     * E.g. R.string.settings_weather_source_weatherzw_instance
     */
    protected abstract val instancePreference: Int

    protected val mApi: ClimWebApi
        get() {
            return jsonClient
                .baseUrl(instance)
                .build()
                .create(ClimWebApi::class.java)
        }

    // SECONDARY WEATHER SOURCE
    override val supportedFeaturesInSecondary = listOf(
        SourceFeature.FEATURE_ALERT,
        SourceFeature.FEATURE_NORMALS
    )
    override val currentAttribution = null
    override val airQualityAttribution = null
    override val pollenAttribution = null
    override val minutelyAttribution = null

    override fun requestSecondaryWeather(
        context: Context,
        location: Location,
        requestedFeatures: List<SourceFeature>,
    ): Observable<SecondaryWeatherWrapper> {
        return Observable.error(Exception("Unimplemented")) // TODO

        val failedFeatures = mutableListOf<SourceFeature>()
        val alertsResult = if (requestedFeatures.contains(SourceFeature.FEATURE_ALERT)) {
            mApi.getAlerts().onErrorResumeNext {
                failedFeatures.add(SourceFeature.FEATURE_ALERT)
                Observable.just(ClimWebAlertsResult())
            }
        } else {
            Observable.just(ClimWebAlertsResult())
        }
        val normalsResult = if (requestedFeatures.contains(SourceFeature.FEATURE_NORMALS)) {
            val cityId = location.parameters.getOrElse(id) { null }?.getOrElse("city_id") { null }
            if (cityId != null) {
                // TODO
                mApi.getNormals("TODO", cityId).onErrorResumeNext {
                    failedFeatures.add(SourceFeature.FEATURE_NORMALS)
                    Observable.just(emptyList())
                }
            } else {
                failedFeatures.add(SourceFeature.FEATURE_NORMALS)
                Observable.just(emptyList())
            }
        } else {
            Observable.just(emptyList())
        }

        return Observable.zip(
            alertsResult,
            normalsResult
        ) { climWebAlertsResult: ClimWebAlertsResult, climWebNormalsResult: List<ClimWebNormals> ->
            convert(
                climWebAlertsResult,
                climWebNormalsResult,
                failedFeatures
            )
        }
    }

    // Location parameters
    override fun needsLocationParametersRefresh(
        location: Location,
        coordinatesChanged: Boolean,
        features: List<SourceFeature>,
    ): Boolean {
        if (SourceFeature.FEATURE_NORMALS !in features) return false

        if (coordinatesChanged) return true

        val cityId = location.parameters.getOrElse(id) { null }?.getOrElse("city_id") { null }

        return cityId.isNullOrEmpty()
    }

    override fun requestLocationParameters(
        context: Context,
        location: Location,
    ): Observable<Map<String, String>> {
        return Observable.error(Exception("Unimplemented")) // TODO
    }

    // CONFIG
    private val config
        get() = SourceConfigStore(context, id)
    override val isConfigured = true
    override val isRestricted = false
    private var instance: String
        set(value) {
            config.edit().putString("instance", value).apply()
        }
        get() = config.getString("instance", null) ?: baseUrl

    override fun getPreferences(context: Context): List<Preference> {
        return listOf(
            EditTextPreference(
                titleId = instancePreference,
                summary = { _, content ->
                    content.ifEmpty {
                        baseUrl
                    }
                },
                content = instance,
                regex = EditTextPreference.URL_REGEX,
                regexError = context.getString(R.string.settings_source_instance_invalid),
                onValueChanged = {
                    instance = it
                }
            )
        )
    }
}
