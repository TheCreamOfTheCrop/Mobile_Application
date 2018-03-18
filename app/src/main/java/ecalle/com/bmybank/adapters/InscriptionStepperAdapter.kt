package ecalle.com.bmybank.adapters

import android.content.Context
import android.support.v4.app.FragmentManager
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import com.stepstone.stepper.viewmodel.StepViewModel
import ecalle.com.bmybank.R
import ecalle.com.bmybank.fragments.inscription_steps.AvatarStep
import ecalle.com.bmybank.fragments.inscription_steps.ScannerStep
import ecalle.com.bmybank.fragments.inscription_steps.UserInformationsStep

/**
 * Created by Thomas Ecalle on 04/03/2018.
 */
class InscriptionStepperAdapter(fm: FragmentManager, context: Context) : AbstractFragmentStepAdapter(fm, context)
{

    override fun getCount() = 3

    override fun createStep(position: Int): Step = with(position) {
        return when (position)
        {
            0 -> UserInformationsStep()
            1 -> AvatarStep()
            else -> ScannerStep()
        }
    }

    override fun getViewModel(position: Int): StepViewModel =
            with(position)
            {
                return when (position)
                {
                    0 ->
                        StepViewModel.Builder(context)
                                .setBackButtonLabel(R.string.connection)
                                .setEndButtonLabel(R.string.stepper_avatar)
                                .setTitle(R.string.stepper_informations).create()
                    1 ->
                        StepViewModel.Builder(context)
                                .setBackButtonLabel(R.string.stepper_informations)
                                .setEndButtonLabel(R.string.stepper_identity_scanner)
                                .setTitle(R.string.stepper_avatar).create()
                    else ->
                        StepViewModel.Builder(context)
                                .setBackButtonLabel(R.string.stepper_avatar)
                                .setEndButtonLabel(R.string.validate)
                                .setTitle(R.string.stepper_identity_scanner).create()
                }
            }

}