package org.breezyweather.sources.openmeteo

import android.content.Context
import org.breezyweather.R
import org.breezyweather.common.basic.models.options.basic.BaseEnum
import org.breezyweather.common.basic.models.options.basic.Utils

/**
 * List from:
 * https://open-meteo.com/en/docs/
 *
 * Up-to-date as of 2024-11-11
 */
enum class OpenMeteoWeatherModel(
    override val id: String,
) : BaseEnum {
    BEST_MATCH("best_match"),

    ECMWF_IFS04("ecmwf_ifs04"),
    ECMWF_IFS025("ecmwf_ifs025"),
    ECMWF_AIFS025("ecmwf_aifs025"),
    CMA_GRAPES_GLOBAL("cma_grapes_global"),
    BOM_ACCESS_GLOBAL("bom_access_global"),

    NCEP_GFS_SEAMLESS("gfs_seamless"),
    NCEP_GFS_GLOBAL("gfs_global"),
    NCEP_HRRR_US_CONUS("gfs_hrrr"),
    NCEP_NBM_US_CONUS("ncep_nbm_conus"),
    GFS_GRAPHCAST("gfs_graphcast025"),

    JMA_SEAMLESS("jma_seamless"),
    JMA_MSM("jma_msm"),
    JMA_GSM("jma_gsm"),

    DWD_ICON_SEAMLESS("icon_seamless"),
    DWD_ICON_GLOBAL("icon_global"),
    DWD_ICON_EU("icon_eu"),
    DWD_ICON_D2("icon_d2"),

    GEM_SEAMLESS("gem_seamless"),
    GEM_GLOBAL("gem_global"),
    GEM_REGIONAL("gem_regional"),
    GEM_HRDPS_CONTINENTAL("gem_hrdps_continental"),

    METEO_FRANCE_SEAMLESS("meteofrance_seamless"),
    METEO_FRANCE_ARPEGE_WORLD("meteofrance_arpege_world"),
    METEO_FRANCE_ARPEGE_EUROPE("meteofrance_arpege_europe"),
    METEO_FRANCE_AROME_FRANCE("meteofrance_arome_france"),
    METEO_FRANCE_AROME_FRANCE_HD("meteofrance_arome_france_hd"),

    ARPAE_COSMO_SEAMLESS("arpae_cosmo_seamless"),
    ARPAE_COSMO_2I("arpae_cosmo_2i"),
    ARPAE_COSMO_5M("arpae_cosmo_5m"),

    MET_NO_SEAMLESS("metno_seamless"),
    MET_NO_NORDIC("metno_nordic"),

    KNMI_SEAMLESS("knmi_seamless"),
    KNMI_HARMONIE_AROME_EUROPE("knmi_harmonie_arome_europe"),
    KNMI_HARMONIE_AROME_NETHERLANDS("knmi_harmonie_arome_netherlands"),

    DMI_SEAMLESS("dmi_seamless"),
    DMI_HARMONIE_AROME_EUROPE("dmi_harmonie_arome_europe"),

    UKMO_SEAMLESS("ukmo_seamless"),
    UKMO_GLOBAL("ukmo_global_deterministic_10km"),
    UKMO_UK("ukmo_uk_deterministic_2km"),
    ;

    companion object {

        fun getInstance(
            value: String,
        ) = OpenMeteoWeatherModel.entries.firstOrNull {
            it.id == value
        }
    }

    override val valueArrayId = R.array.open_meteo_weather_models_values
    override val nameArrayId = R.array.open_meteo_weather_models

    override fun getName(context: Context) =
        Utils.getName(context, this)
            .replace(
                "Best match",
                context.getString(R.string.settings_weather_source_open_meteo_weather_models_best_match)
            )
            .replace(
                "Seamless",
                context.getString(R.string.settings_weather_source_open_meteo_weather_models_seamless)
            )

    fun getDescription(context: Context): String? = if (id == "best_match") {
        context.getString(R.string.settings_weather_source_open_meteo_weather_models_best_match_description)
    } else if (id.endsWith("_seamless")) {
        context.getString(R.string.settings_weather_source_open_meteo_weather_models_seamless_description)
    } else {
        null
    }
}
