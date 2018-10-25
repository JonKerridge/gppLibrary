/**
 * Package jcsp.gppLibrary.tests.scripts
 * defines scripts used to test the components<p>
 *
 * <b>The scripts test the components.</b>
 * The script AllTests runs all the tests<p>
 *
 * Test1  Emit  Collect<br>
 * Test2  Emit Worker Collect<br>
 * Test3  Emit CombineNto1 Collect<br>
 * Test4  Emit CombineNto1 EmitFromInput Collect<br>
 * Test5  Emit OneFanAny AnyFanOne Collect
 * Test6  Emit OneFanAny AnyFanAny AnyFanOne Collect
 * Test7  Emit OneFanAny AnySeqCastAny AnyFanOne Collect
 * Test8  Emit OneFanList ListFanOne Collect
 * Test8a  Emit OneFanList ListMergeOne Collect
 * Test9  Emit OneParCastList ListParOne Collect
 * Test10 Emit OneSeqCastAny AnyFanOne Collect
 * Test11 Emit OneSeqCastList ListSeqOne Collect
 * Test12 Emit OneParCastList ListParOne Collect
 * Test13 Emit OneParCastList ListFanOne Collect
 * Test14 Emit OneSeqCastList ListFanOne Collect
 * Test15 Emit OneFanRequestedAny PAR(RequestingFanAny) AnyFanOne Collect
 * Test16 Emit OneFanRequestedAny PAR(RequestingFanList ListFanOne) AnyFanOne Collect
 * Test17 Emit OneFanRequestedAny PAR(RequestingSeqCastAny NullTestWorker) AnyFanOne Collect
 * Test18 Emit OneFanRequestedAny PAR(RequestingParCastList NullTestWorker) AnyFanOne Collect
 * Test19 Emit OneFanRequestedAny PAR(RequestingSeqCastList NullTestWorker) AnyFanOne Collect
 * Test20 Emit OneFanAny AnyGroupList ListGroupList ListGroupAny AnyFanOne Collect
 * Test21 Emit OneFanList ListGroupList ListGroupList ListGroupAny AnyFanOne Collect
 * Test22 Emit OneFanList ListGroupList ListGroupList ListGroupList ListFanOne Collect
 * Test23 Emit OneSeqCastList ListGroupList ListGroupList ListGroupList ListFanOne Collect
 * Test24 Emit OneSeqCastList ListGroupList ListGroupList-sync ListGroupList ListFanOne Collect
 * Test23a Emit OneSeqCastList ListGroupList ListGroupList ListGroupList ListMergeOne Collect
 * Test24a Emit OneSeqCastList ListGroupList ListGroupList-sync ListGroupList ListMergeOne Collect
 * Test25 Emit OneSeqCastList ListGroupList ListGroupList-sync ListGroupList ListParOne Collect
 * Test26 Emit OneSeqCastList ListGroupList ListGroupList-sync ListGroupList ListSeqOne Collect
 * Test27 Emit OneParCastList ListGroupList ListGroupList-sync ListGroupList ListSeqOne Collect
 * Test28 Emit OneParCastList ListGroupList-sync ListGroupList-sync ListGroupList-sync ListSeqOne Collect
 * Test29 Emit OneSeqCastAny AnyGroupList-sync ListGroupList-sync ListGroupList-sync ListSeqOne Collect
 * Test30 Emit OneFanAny AnyGroupAny AnyGroupAny AnyGroupAny AnyFanOne Collect
 * Test31 Emit OnePipelineOne Collect
 * Test32 Emit OnePipelineCollect
 * Test33 Emit OneFanAny GroupOfPipelineCollects
 * Test34 Emit OneSeqCastAny PipelineOfGroupCollectsCollects
 * Test35 Emit OneFanList GroupOfPipelines ListFanOne Collect
 * Test35a Emit OneFanList GroupOfPipelines ListMergeOne Collect
 * Test36 Emit OneFanAny PipelineOfGroups AnyFanOne Collect
 * Test37 Emit ThreePhaseWorker Collect
 * Test38 EmitWithLocal Collect
 * Test39 EmitWithFeedback Worker FeedbackBool Collect
 * Test40 EmitWithFeedback ofa AnyGroupAny afo FeedbackBool Collect
 *
 * <p>
 * Author Information, Copyright and License are held in the file
 * <a href="CopyrightandLicense">Copyright and License</a>
 *
  */

package gppJunitTests;