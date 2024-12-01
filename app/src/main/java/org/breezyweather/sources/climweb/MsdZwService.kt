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
import dagger.hilt.android.qualifiers.ApplicationContext
import org.breezyweather.R
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

/**
 * Zimbabwe
 */
class MsdZwService @Inject constructor(
    @ApplicationContext injectedContext: Context,
    @Named("JsonClient") val injectedJsonClient: Retrofit.Builder,
) : ClimWebService() {

    override val id = "msdzw"
    override val name = "MSD Zimbabwe"
    override val privacyPolicyUrl = ""

    override val context = injectedContext
    override val jsonClient = injectedJsonClient
    override val baseUrl = "https://www.weatherzw.org.zw/"
    override val instancePreference = R.string.settings_weather_source_weatherzw_instance

    override val alertAttribution = "Meteorological Services Department Of Zimbabwe"
    override val normalsAttribution = alertAttribution
}
