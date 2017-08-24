package scheduler;//####[1]####
//####[1]####
import java.util.ArrayList;//####[3]####
import java.util.Collections;//####[4]####
import java.util.HashSet;//####[5]####
import java.util.List;//####[6]####
import java.util.Set;//####[7]####
import java.util.concurrent.PriorityBlockingQueue;//####[8]####
import java.util.concurrent.ConcurrentLinkedQueue;//####[9]####
import graph.Graph;//####[11]####
import graph.Vertex;//####[12]####
import gui.ScheduleListener;//####[13]####
import gui.Visualiser;//####[14]####
import gui.VisualiserController;//####[15]####
import heuristics.CostFunctionCalculator;//####[16]####
import pruning.ListScheduling;//####[17]####
import pruning.Pruning;//####[18]####
import components.ScheduleComparator;//####[19]####
//####[19]####
//-- ParaTask related imports//####[19]####
import pt.runtime.*;//####[19]####
import java.util.concurrent.ExecutionException;//####[19]####
import java.util.concurrent.locks.*;//####[19]####
import java.lang.reflect.*;//####[19]####
import pt.runtime.GuiThread;//####[19]####
import java.util.concurrent.BlockingQueue;//####[19]####
import java.util.ArrayList;//####[19]####
import java.util.List;//####[19]####
//####[19]####
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
 *///####[31]####
public class ParallelisedScheduler {//####[32]####
    static{ParaTask.init();}//####[32]####
    /*  ParaTask helper method to access private/protected slots *///####[32]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[32]####
        if (m.getParameterTypes().length == 0)//####[32]####
            m.invoke(instance);//####[32]####
        else if ((m.getParameterTypes().length == 1))//####[32]####
            m.invoke(instance, arg);//####[32]####
        else //####[32]####
            m.invoke(instance, arg, interResult);//####[32]####
    }//####[32]####
//####[33]####
    private int _numberOfProcessors;//####[33]####
//####[34]####
    private PriorityBlockingQueue<Schedule> _openSchedules;//####[34]####
//####[35]####
    private ConcurrentLinkedQueue<Schedule> _closedSchedules;//####[35]####
//####[36]####
    private List<Schedule> _partialExpanded;//####[36]####
//####[37]####
    private List<Schedule> _finalSchedule;//####[37]####
//####[38]####
    private List<ScheduleListener> _listeners;//####[38]####
//####[39]####
    private boolean _visualisation;//####[39]####
//####[40]####
    private int _upperBoundCost;//####[40]####
//####[41]####
    private int _numberOfCores;//####[41]####
//####[43]####
    public ParallelisedScheduler(int numberOfProcessors, int numberOfCores, boolean visualisation) {//####[43]####
        _openSchedules = new PriorityBlockingQueue<Schedule>(Graph.getInstance().getVertices().size(), new ScheduleComparator());//####[44]####
        _closedSchedules = new ConcurrentLinkedQueue<Schedule>();//####[45]####
        _partialExpanded = new ArrayList<Schedule>();//####[46]####
        _finalSchedule = new ArrayList<Schedule>();//####[47]####
        _numberOfProcessors = numberOfProcessors;//####[48]####
        ListScheduling ls = new ListScheduling(_numberOfProcessors);//####[49]####
        _upperBoundCost = ls.getUpperBoundCostFunction();//####[50]####
        _visualisation = visualisation;//####[51]####
        _numberOfCores = numberOfCores;//####[52]####
        if (_visualisation) //####[53]####
        {//####[53]####
            Visualiser visualiser = new Visualiser();//####[54]####
            VisualiserController visualiserController = new VisualiserController(visualiser);//####[55]####
            _listeners = new ArrayList<ScheduleListener>();//####[56]####
            _listeners.add(visualiserController);//####[57]####
        }//####[58]####
    }//####[59]####
//####[66]####
    /**
	 * This method returns the optimal schedule
	 * @return void
	 * @throws Exception
	 *///####[66]####
    public Schedule getOptimalSchedule() throws Exception {//####[66]####
        this.addRootVerticesSchedulesToOpenSchedule();//####[67]####
        Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();//####[68]####
        return optimalSchedule;//####[69]####
    }//####[70]####
//####[79]####
    /**
	 * This method uses the A* algorithm to create schedules 
	 * It only returns back once it finds an optimal schedule
	 * It throws an exception if the openschedule queue is empty because that is not suppose to happen
	 * 
	 * @return optimal schedule
	 *///####[79]####
    private Schedule makeSchedulesUsingAlgorithm() {//####[79]####
        if (_numberOfCores == -1) //####[80]####
        {//####[80]####
            while (_finalSchedule.isEmpty()) //####[81]####
            {//####[81]####
                searchAndExpand();//####[82]####
            }//####[83]####
            return _finalSchedule.get(0);//####[84]####
        } else {//####[85]####
            while (_finalSchedule.isEmpty() && (_openSchedules.size() < _numberOfCores)) //####[86]####
            {//####[86]####
                searchAndExpand();//####[87]####
            }//####[88]####
            if (_finalSchedule.isEmpty()) //####[89]####
            {//####[89]####
                TaskIDGroup g = paralleliseSearch(_openSchedules);//####[90]####
                try {//####[91]####
                    g.waitTillFinished();//####[92]####
                } catch (ExecutionException e) {//####[93]####
                    e.printStackTrace();//####[94]####
                } catch (InterruptedException e) {//####[95]####
                    e.printStackTrace();//####[96]####
                }//####[97]####
            }//####[98]####
            return _finalSchedule.get(0);//####[99]####
        }//####[100]####
    }//####[101]####
//####[102]####
    private static volatile Method __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = null;//####[102]####
    private synchronized static void __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet() {//####[102]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[102]####
            try {//####[102]####
                __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__paralleliseSearch", new Class[] {//####[102]####
                    PriorityBlockingQueue.class//####[102]####
                });//####[102]####
            } catch (Exception e) {//####[102]####
                e.printStackTrace();//####[102]####
            }//####[102]####
        }//####[102]####
    }//####[102]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[102]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[102]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[102]####
    }//####[102]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules, TaskInfo taskinfo) {//####[102]####
        // ensure Method variable is set//####[102]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[102]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[102]####
        }//####[102]####
        taskinfo.setParameters(_openSchedules);//####[102]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[102]####
        taskinfo.setInstance(this);//####[102]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[102]####
    }//####[102]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[102]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[102]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[102]####
    }//####[102]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[102]####
        // ensure Method variable is set//####[102]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[102]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[102]####
        }//####[102]####
        taskinfo.setTaskIdArgIndexes(0);//####[102]####
        taskinfo.addDependsOn(_openSchedules);//####[102]####
        taskinfo.setParameters(_openSchedules);//####[102]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[102]####
        taskinfo.setInstance(this);//####[102]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[102]####
    }//####[102]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[102]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[102]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[102]####
    }//####[102]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[102]####
        // ensure Method variable is set//####[102]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[102]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[102]####
        }//####[102]####
        taskinfo.setQueueArgIndexes(0);//####[102]####
        taskinfo.setIsPipeline(true);//####[102]####
        taskinfo.setParameters(_openSchedules);//####[102]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[102]####
        taskinfo.setInstance(this);//####[102]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[102]####
    }//####[102]####
    public void __pt__paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[102]####
        while (_finalSchedule.isEmpty()) //####[103]####
        {//####[103]####
            searchAndExpand();//####[104]####
        }//####[105]####
    }//####[106]####
//####[106]####
//####[107]####
    private void searchAndExpand() {//####[107]####
        Schedule currentSchedule = _openSchedules.poll();//####[108]####
        if (_visualisation) //####[109]####
        {//####[109]####
            fireScheduleChangeEvent(currentSchedule);//####[110]####
        }//####[111]####
        _closedSchedules.add(currentSchedule);//####[112]####
        if (this.hasScheduleUsedAllPossibleVertices(currentSchedule)) //####[113]####
        {//####[113]####
            _finalSchedule.add(currentSchedule);//####[114]####
        }//####[115]####
        this.addCurrentSchedulePossibleSuccessorsToOpenSchedule(currentSchedule);//####[116]####
    }//####[117]####
//####[127]####
    /**
	 * For the current schedule we are processing,
	 * it tries to find successor schedules that are available
	 * Those successors schedules are then added to the open schedule if it
	 * passes the conditions required 
	 *
	 * @param currentSchedule
	 *///####[127]####
    private void addCurrentSchedulePossibleSuccessorsToOpenSchedule(Schedule currentSchedule) {//####[127]####
        List<Vertex> currentVertexSuccessors = currentSchedule.getChildVertices();//####[128]####
        for (Vertex childVertex : currentVertexSuccessors) //####[129]####
        {//####[129]####
            Schedule currentScheduleCopy = new Schedule(currentSchedule);//####[131]####
            Schedule[] currentChildVertexSchedules = new Schedule[_numberOfProcessors];//####[132]####
            currentChildVertexSchedules = currentScheduleCopy.generateAllPossibleScheduleForSpecifiedVertex(childVertex);//####[134]####
            CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();//####[136]####
            int parentScheduleCost = costFunctionCalculator.getTotalCostFunction(currentSchedule);//####[137]####
            if (_partialExpanded.contains(currentSchedule)) //####[138]####
            {//####[138]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[139]####
                {//####[139]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[140]####
                    if (childScheduleCost <= _upperBoundCost) //####[142]####
                    {//####[142]####
                        if (parentScheduleCost > costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i])) //####[143]####
                        {//####[143]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[144]####
                            {//####[144]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[145]####
                            }//####[146]####
                        }//####[147]####
                    }//####[148]####
                }//####[149]####
            } else {//####[150]####
                boolean partialExpanded = false;//####[151]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[152]####
                {//####[152]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[153]####
                    if (parentScheduleCost >= childScheduleCost) //####[154]####
                    {//####[154]####
                        if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[155]####
                        {//####[155]####
                            _openSchedules.add(currentChildVertexSchedules[i]);//####[156]####
                            partialExpanded = true;//####[157]####
                        }//####[158]####
                    }//####[159]####
                }//####[160]####
                if (!partialExpanded) //####[161]####
                {//####[161]####
                    for (int i = 0; i < _numberOfProcessors; i++) //####[162]####
                    {//####[162]####
                        int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[163]####
                        if (childScheduleCost <= _upperBoundCost) //####[165]####
                        {//####[165]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[166]####
                            {//####[166]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[167]####
                            }//####[168]####
                        }//####[169]####
                    }//####[170]####
                } else {//####[171]####
                    _openSchedules.add(currentSchedule);//####[172]####
                    _closedSchedules.remove(currentSchedule);//####[173]####
                    _partialExpanded.add(currentSchedule);//####[174]####
                }//####[175]####
            }//####[176]####
        }//####[177]####
    }//####[178]####
//####[192]####
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
	 *///####[192]####
    private boolean checkScheduleThroughPruning(Schedule childSchedule) {//####[192]####
        Pruning pruning = new Pruning();//####[193]####
        if (pruning.isCurrentScheduleNeeded(_openSchedules, _closedSchedules, childSchedule)) //####[194]####
        {//####[194]####
            return true;//####[195]####
        }//####[196]####
        return false;//####[197]####
    }//####[199]####
//####[209]####
    /**
	 * This method checks if the current schedule is a finished schedule
	 *
	 * returns true if it is
	 * otherwise returns false
	 *
	 * @param currentSchedule
	 * @return
	 *///####[209]####
    private boolean hasScheduleUsedAllPossibleVertices(Schedule currentSchedule) {//####[209]####
        List<Vertex> currentScheduleUsedVertices = currentSchedule.getAllUsedVertices();//####[210]####
        for (Vertex vertex : Graph.getInstance().getVertices()) //####[211]####
        {//####[211]####
            if (!currentScheduleUsedVertices.contains(vertex)) //####[212]####
            {//####[212]####
                return false;//####[213]####
            }//####[214]####
        }//####[215]####
        return true;//####[216]####
    }//####[217]####
//####[226]####
    /**
	 * This method adds root schedules to openschedules
	 * since at the start of schedule the first task is the same no matter which processor
	 * it is put on so only one variation of root schedule is added.
	 *
	 *///####[226]####
    private void addRootVerticesSchedulesToOpenSchedule() {//####[226]####
        for (Vertex rootVertex : Graph.getInstance().getRootVertices()) //####[227]####
        {//####[227]####
            Schedule emptySchedule = new Schedule(_numberOfProcessors);//####[228]####
            Schedule[] rootSchedules = new Schedule[_numberOfProcessors];//####[229]####
            rootSchedules = emptySchedule.generateAllPossibleScheduleForSpecifiedVertex(rootVertex);//####[231]####
            _openSchedules.add(rootSchedules[0]);//####[233]####
        }//####[234]####
    }//####[235]####
//####[236]####
    private void fireScheduleChangeEvent(Schedule currentSchedule) {//####[236]####
        for (ScheduleListener listener : _listeners) //####[237]####
        {//####[237]####
            listener.update(currentSchedule);//####[238]####
        }//####[239]####
    }//####[240]####
}//####[240]####
