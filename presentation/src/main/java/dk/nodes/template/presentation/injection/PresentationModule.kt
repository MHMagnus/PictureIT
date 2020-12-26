package dk.nodes.template.presentation.injection

import dagger.Module
import dk.nodes.template.presentation.ui.history.HistoryBuilder
import dk.nodes.template.presentation.ui.positive.PositiveListBuilder
import dk.nodes.template.presentation.ui.home.HomeBuilder
import dk.nodes.template.presentation.ui.main.MainActivityBuilder
import dk.nodes.template.presentation.ui.negative.NegativeListBuilder
import dk.nodes.template.presentation.ui.picture_detail.PictureDetailBuilder
import dk.nodes.template.presentation.ui.results.ResultsBuilder
import dk.nodes.template.presentation.ui.info.InfoActivityBuilder
import dk.nodes.template.presentation.ui.loadmodel.LoadModelBuilder
import dk.nodes.template.presentation.ui.savemodel.SaveModelBuilder

@Module(includes = [
    MainActivityBuilder::class,
    PictureDetailBuilder::class,
    PositiveListBuilder::class,
    ResultsBuilder::class,
    HomeBuilder::class,
    ResultsBuilder::class,
    NegativeListBuilder::class,
    HistoryBuilder::class,
    InfoActivityBuilder::class,
    SaveModelBuilder::class,
    LoadModelBuilder::class

])
class PresentationModule