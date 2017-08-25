package scheduler;//####[1]####
//####[1]####
import java.util.ArrayList;//####[3]####
import java.util.List;//####[4]####
import java.util.concurrent.ConcurrentLinkedQueue;//####[5]####
import java.util.concurrent.PriorityBlockingQueue;//####[6]####
import graph.Graph;//####[8]####
import graph.Vertex;//####[9]####
import gui.ScheduleListener;//####[11]####
import gui.VisualiserController;//####[12]####
import heuristics.CostFunctionCalculator;//####[13]####
import pruning.ListScheduling;//####[14]####
import pruning.Pruning;//####[15]####
import components.ScheduleComparator;//####[16]####
import data.Data;
//####[16]####
//-- ParaTask related imports//####[16]####
import pt.runtime.*;//####[16]####
import java.util.concurrent.ExecutionException;//####[16]####
import java.util.concurrent.locks.*;//####[16]####
import java.lang.reflect.*;//####[16]####
import pt.runtime.GuiThread;//####[16]####
import java.util.concurrent.BlockingQueue;//####[16]####
import java.util.ArrayList;//####[16]####
import java.util.List;//####[16]####
//####[16]####
/**
 * This Class uses the Schedule Class and Processor Class
 * to make schedules using the information from the Graph Variable which
 * contains all the list of vertex and edges.
 *
 * The schedules are created using A* algorithm.
 *
 * This Class manages the created schedules and returns the optimal schedule
 *
 * @author Alex Yoo
 *
 *///####[28]####
public class ParallelisedScheduler {//####[29]####
    static{ParaTask.init();}//####[29]####
    /*  ParaTask helper method to access private/protected slots *///####[29]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[29]####
        if (m.getParameterTypes().length == 0)//####[29]####
            m.invoke(instance);//####[29]####
        else if ((m.getParameterTypes().length == 1))//####[29]####
            m.invoke(instance, arg);//####[29]####
        else //####[29]####
            m.invoke(instance, arg, interResult);//####[29]####
    }//####[29]####
//####[30]####
    private int _numberOfProcessors;//####[30]####
//####[31]####
    private PriorityBlockingQueue<Schedule> _openSchedules;//####[31]####
//####[32]####
    private ConcurrentLinkedQueue<Schedule> _closedSchedules;//####[32]####
//####[33]####
    private List<Schedule> _partialExpanded;//####[33]####
//####[34]####
    private List<Schedule> _finalSchedule;//####[34]####
//####[35]####
    private int _upperBoundCost;//####[35]####
//####[36]####
    private int _numberOfCores;//####[36]####
//####[37]####
    private boolean _visualisation;//####[37]####
//####[38]####
    private List<ScheduleListener> _listeners;//####[38]####
//####[39]####
    private Data _data;//####[39]####
//####[41]####
    public ParallelisedScheduler(int numberOfProcessors, int numberOfCores, boolean visualisation) {//####[41]####
        _openSchedules = new PriorityBlockingQueue<Schedule>(Graph.getInstance().getVertices().size(), new ScheduleComparator());//####[42]####
        _closedSchedules = new ConcurrentLinkedQueue<Schedule>();//####[43]####
        _partialExpanded = new ArrayList<Schedule>();//####[44]####
        _finalSchedule = new ArrayList<Schedule>();//####[45]####
        _numberOfProcessors = numberOfProcessors;//####[46]####
        _numberOfCores = numberOfCores;//####[47]####
        ListScheduling ls = new ListScheduling(_numberOfProcessors);//####[48]####
        _upperBoundCost = ls.getUpperBoundCostFunction();//####[49]####
        _visualisation = visualisation;//####[50]####
        if (_visualisation) //####[51]####
        {//####[51]####
            _data = new Data();//####[52]####
            VisualiserController visualiserController = new VisualiserController(_data);//####[53]####
            _listeners = new ArrayList<ScheduleListener>();//####[54]####
            _listeners.add(visualiserController);//####[55]####
        }//####[56]####
    }//####[57]####
//####[64]####
    /**
	 * This method returns the optimal schedule
	 * @return void
	 * @throws Exception
	 *///####[64]####
    public Schedule getOptimalSchedule() {//####[64]####
        if (_visualisation) //####[65]####
        {//####[65]####
            _data.setStartTime();//####[66]####
            this.addRootVerticesSchedulesToOpenSchedule();//####[67]####
            Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();//####[68]####
            _data.isFinished(true);//####[69]####
            fireScheduleChangeEvent();//####[70]####
            return optimalSchedule;//####[71]####
        } else {//####[72]####
            this.addRootVerticesSchedulesToOpenSchedule();//####[73]####
            Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();//####[74]####
            return optimalSchedule;//####[75]####
        }//####[76]####
    }//####[77]####
//####[86]####
    /**
	 * This method uses the A* algorithm to create schedules 
	 * It only returns back once it finds an optimal schedule
	 * It throws an exception if the openschedule queue is empty because that is not suppose to happen
	 * 
	 * @return optimal schedule
	 *///####[86]####
    private Schedule makeSchedulesUsingAlgorithm() {//####[86]####
        if (_numberOfCores == -1) //####[87]####
        {//####[87]####
            while (_finalSchedule.isEmpty()) //####[88]####
            {//####[88]####
                searchAndExpand();//####[89]####
            }//####[90]####
            return _finalSchedule.get(0);//####[91]####
        } else {//####[92]####
            while (_finalSchedule.isEmpty() && (_openSchedules.size() < _numberOfCores)) //####[93]####
            {//####[93]####
                searchAndExpand();//####[94]####
            }//####[95]####
            if (_finalSchedule.isEmpty()) //####[96]####
            {//####[96]####
                TaskIDGroup g = paralleliseSearch(_openSchedules);//####[97]####
                try {//####[98]####
                    g.waitTillFinished();//####[99]####
                } catch (ExecutionException e) {//####[100]####
                    e.printStackTrace();//####[101]####
                } catch (InterruptedException e) {//####[102]####
                    e.printStackTrace();//####[103]####
                }//####[104]####
            }//####[105]####
            return _finalSchedule.get(0);//####[106]####
        }//####[107]####
    }//####[108]####
//####[109]####
    private void searchAndExpand() {//####[109]####
        Schedule currentSchedule = _openSchedules.poll();//####[110]####
        if (_visualisation) //####[111]####
        {//####[111]####
            _data.updateTotalNumberOfCreatedSchedules(_openSchedules.size() + _closedSchedules.size());//####[112]####
            _data.updateCurrentSchedule(currentSchedule);//####[113]####
            fireScheduleChangeEvent();//####[114]####
        }//####[115]####
        _closedSchedules.add(currentSchedule);//####[116]####
        if (this.hasScheduleUsedAllPossibleVertices(currentSchedule)) //####[117]####
        {//####[117]####
            _finalSchedule.add(currentSchedule);//####[118]####
        }//####[119]####
        this.addCurrentSchedulePossibleSuccessorsToOpenSchedule(currentSchedule);//####[120]####
    }//####[121]####
//####[122]####
    private static volatile Method __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = null;//####[122]####
    private synchronized static void __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet() {//####[122]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[122]####
            try {//####[122]####
                __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__paralleliseSearch", new Class[] {//####[122]####
                    PriorityBlockingQueue.class//####[122]####
                });//####[122]####
            } catch (Exception e) {//####[122]####
                e.printStackTrace();//####[122]####
            }//####[122]####
        }//####[122]####
    }//####[122]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[122]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[122]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[122]####
    }//####[122]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules, TaskInfo taskinfo) {//####[122]####
        // ensure Method variable is set//####[122]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[122]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[122]####
        }//####[122]####
        taskinfo.setParameters(_openSchedules);//####[122]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[122]####
        taskinfo.setInstance(this);//####[122]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[122]####
    }//####[122]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[122]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[122]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[122]####
    }//####[122]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[122]####
        // ensure Method variable is set//####[122]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[122]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[122]####
        }//####[122]####
        taskinfo.setTaskIdArgIndexes(0);//####[122]####
        taskinfo.addDependsOn(_openSchedules);//####[122]####
        taskinfo.setParameters(_openSchedules);//####[122]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[122]####
        taskinfo.setInstance(this);//####[122]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[122]####
    }//####[122]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[122]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[122]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[122]####
    }//####[122]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[122]####
        // ensure Method variable is set//####[122]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[122]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[122]####
        }//####[122]####
        taskinfo.setQueueArgIndexes(0);//####[122]####
        taskinfo.setIsPipeline(true);//####[122]####
        taskinfo.setParameters(_openSchedules);//####[122]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[122]####
        taskinfo.setInstance(this);//####[122]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[122]####
    }//####[122]####
    public void __pt__paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[122]####
        while (_finalSchedule.isEmpty()) //####[123]####
        {//####[123]####
            searchAndExpand();//####[124]####
        }//####[125]####
    }//####[126]####
//####[126]####
//####[136]####
    /**
	 * For the current schedule we are processing,
	 * it tries to find successor schedules that are available
	 * Those successors schedules are then added to the open schedule if it
	 * passes the conditions required 
	 *
	 * @param currentSchedule
	 *///####[136]####
    private void addCurrentSchedulePossibleSuccessorsToOpenSchedule(Schedule currentSchedule) {//####[136]####
        List<Vertex> currentVertexSuccessors = currentSchedule.getChildVertices();//####[137]####
        for (Vertex childVertex : currentVertexSuccessors) //####[138]####
        {//####[138]####
            Schedule currentScheduleCopy = new Schedule(currentSchedule);//####[140]####
            Schedule[] currentChildVertexSchedules = new Schedule[_numberOfProcessors];//####[141]####
            currentChildVertexSchedules = currentScheduleCopy.generateAllPossibleScheduleForSpecifiedVertex(childVertex);//####[143]####
            CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();//####[145]####
            int parentScheduleCost = costFunctionCalculator.getTotalCostFunction(currentSchedule);//####[146]####
            if (_partialExpanded.contains(currentSchedule)) //####[148]####
            {//####[148]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[149]####
                {//####[149]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[150]####
                    if (childScheduleCost <= _upperBoundCost) //####[152]####
                    {//####[152]####
                        if (parentScheduleCost > costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i])) //####[153]####
                        {//####[153]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[154]####
                            {//####[154]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[155]####
                            }//####[156]####
                        }//####[157]####
                    }//####[158]####
                }//####[159]####
            } else {//####[160]####
                boolean partialExpanded = false;//####[161]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[162]####
                {//####[162]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[163]####
                    if (parentScheduleCost >= childScheduleCost) //####[164]####
                    {//####[164]####
                        if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[165]####
                        {//####[165]####
                            _openSchedules.add(currentChildVertexSchedules[i]);//####[166]####
                            partialExpanded = true;//####[167]####
                        }//####[168]####
                    }//####[169]####
                }//####[170]####
                if (!partialExpanded) //####[171]####
                {//####[171]####
                    for (int i = 0; i < _numberOfProcessors; i++) //####[172]####
                    {//####[172]####
                        int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[173]####
                        if (childScheduleCost <= _upperBoundCost) //####[175]####
                        {//####[175]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[176]####
                            {//####[176]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[177]####
                            }//####[178]####
                        }//####[179]####
                    }//####[180]####
                } else {//####[181]####
                    _openSchedules.add(currentSchedule);//####[182]####
                    _closedSchedules.remove(currentSchedule);//####[183]####
                    _partialExpanded.add(currentSchedule);//####[184]####
                }//####[185]####
            }//####[186]####
        }//####[187]####
    }//####[188]####
//####[202]####
    /**
	 * This method checks if the successor schedules have the conditions required to
	 * get added into the openschedule.
	 * It also checks inside the openschedule and closedschedule if there are any schedules that can be taken out
	 * that was made redundant by predecessor schedules
	 *
	 * returns true if it passes
	 * otherwise returns false
	 *
	 * @param childSchedule
	 * @return
	 *///####[202]####
    private boolean checkScheduleThroughPruning(Schedule childSchedule) {//####[202]####
        Pruning pruning = new Pruning();//####[203]####
        if (pruning.isCurrentScheduleNeeded(_openSchedules, _closedSchedules, childSchedule)) //####[204]####
        {//####[204]####
            return true;//####[205]####
        }//####[206]####
        return false;//####[207]####
    }//####[209]####
//####[219]####
    /**
	 * This method checks if the current schedule is a finished schedule
	 *
	 * returns true if it is
	 * otherwise returns false
	 *
	 * @param currentSchedule
	 * @return
	 *///####[219]####
    private boolean hasScheduleUsedAllPossibleVertices(Schedule currentSchedule) {//####[219]####
        List<Vertex> currentScheduleUsedVertices = currentSchedule.getAllUsedVertices();//####[220]####
        for (Vertex vertex : Graph.getInstance().getVertices()) //####[221]####
        {//####[221]####
            if (!currentScheduleUsedVertices.contains(vertex)) //####[222]####
            {//####[222]####
                return false;//####[223]####
            }//####[224]####
        }//####[225]####
        return true;//####[226]####
    }//####[227]####
//####[236]####
    /**
	 * This method adds root schedules to openschedules
	 * since at the start of schedule the first _timerTask is the same no matter which processor
	 * it is put on so only one variation of root schedule is added.
	 *
	 *///####[236]####
    private void addRootVerticesSchedulesToOpenSchedule() {//####[236]####
        for (Vertex rootVertex : Graph.getInstance().getRootVertices()) //####[237]####
        {//####[237]####
            Schedule emptySchedule = new Schedule(_numberOfProcessors);//####[238]####
            Schedule[] rootSchedules = new Schedule[_numberOfProcessors];//####[239]####
            rootSchedules = emptySchedule.generateAllPossibleScheduleForSpecifiedVertex(rootVertex);//####[241]####
            _openSchedules.add(rootSchedules[0]);//####[243]####
        }//####[244]####
    }//####[245]####
//####[246]####
    private void fireScheduleChangeEvent() {//####[246]####
        for (ScheduleListener listener : _listeners) //####[247]####
        {//####[247]####
            listener.update();//####[248]####
        }//####[249]####
    }//####[250]####
}//####[250]####
