package scheduler;//####[1]####
//####[1]####
import java.util.ArrayList;//####[3]####
import java.util.HashSet;//####[4]####
import java.util.List;//####[5]####
import java.util.Set;//####[6]####
import java.util.concurrent.PriorityBlockingQueue;//####[7]####
import java.util.concurrent.ConcurrentLinkedQueue;//####[8]####
import graph.Graph;//####[10]####
import graph.Vertex;//####[11]####
import gui.ScheduleListener;//####[12]####
import gui.Visualiser;//####[13]####
import gui.VisualiserController;//####[14]####
import heuristics.CostFunctionCalculator;//####[15]####
import pruning.ListScheduling;//####[16]####
import pruning.Pruning;//####[17]####
import components.ScheduleComparator;//####[18]####
//####[18]####
//-- ParaTask related imports//####[18]####
import pt.runtime.*;//####[18]####
import java.util.concurrent.ExecutionException;//####[18]####
import java.util.concurrent.locks.*;//####[18]####
import java.lang.reflect.*;//####[18]####
import pt.runtime.GuiThread;//####[18]####
import java.util.concurrent.BlockingQueue;//####[18]####
import java.util.ArrayList;//####[18]####
import java.util.List;//####[18]####
//####[18]####
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
 *///####[30]####
public class ParallelisedScheduler {//####[31]####
    static{ParaTask.init();}//####[31]####
    /*  ParaTask helper method to access private/protected slots *///####[31]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[31]####
        if (m.getParameterTypes().length == 0)//####[31]####
            m.invoke(instance);//####[31]####
        else if ((m.getParameterTypes().length == 1))//####[31]####
            m.invoke(instance, arg);//####[31]####
        else //####[31]####
            m.invoke(instance, arg, interResult);//####[31]####
    }//####[31]####
//####[32]####
    private int _numberOfProcessors;//####[32]####
//####[33]####
    private PriorityBlockingQueue<Schedule> _openSchedules;//####[33]####
//####[34]####
    private ConcurrentLinkedQueue<Schedule> _closedSchedules;//####[34]####
//####[35]####
    private List<Schedule> _finalSchedule;//####[35]####
//####[36]####
    private List<ScheduleListener> _listeners;//####[36]####
//####[37]####
    private boolean _visualisation;//####[37]####
//####[38]####
    private int _upperBoundCost;//####[38]####
//####[39]####
    private int _numberOfCores;//####[39]####
//####[41]####
    public ParallelisedScheduler(int numberOfProcessors, int numberOfCores, boolean visualisation) {//####[41]####
        _openSchedules = new PriorityBlockingQueue<Schedule>(Graph.getInstance().getVertices().size(), new ScheduleComparator());//####[42]####
        _closedSchedules = new ConcurrentLinkedQueue<Schedule>();//####[43]####
        _finalSchedule = new ArrayList<Schedule>();//####[44]####
        _numberOfProcessors = numberOfProcessors;//####[45]####
        ListScheduling ls = new ListScheduling(_numberOfProcessors);//####[46]####
        _upperBoundCost = ls.getUpperBoundCostFunction();//####[47]####
        _visualisation = visualisation;//####[48]####
        _numberOfCores = numberOfCores;//####[49]####
        if (_visualisation) //####[50]####
        {//####[50]####
            Visualiser visualiser = new Visualiser();//####[51]####
            VisualiserController visualiserController = new VisualiserController(visualiser);//####[52]####
            _listeners = new ArrayList<ScheduleListener>();//####[53]####
            _listeners.add(visualiserController);//####[54]####
        }//####[55]####
    }//####[56]####
//####[63]####
    /**
	 * This method returns the optimal schedule
	 * @return void
	 * @throws Exception
	 *///####[63]####
    public Schedule getOptimalSchedule() throws Exception {//####[63]####
        this.addRootVerticesSchedulesToOpenSchedule();//####[64]####
        Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();//####[65]####
        return optimalSchedule;//####[66]####
    }//####[67]####
//####[76]####
    /**
	 * This method uses the A* algorithm to create schedules 
	 * It only returns back once it finds an optimal schedule
	 * It throws an exception if the openschedule queue is empty because that is not suppose to happen
	 * 
	 * @return optimal schedule
	 *///####[76]####
    private Schedule makeSchedulesUsingAlgorithm() {//####[76]####
        if (_numberOfCores == -1) //####[77]####
        {//####[77]####
            while (_finalSchedule.isEmpty()) //####[78]####
            {//####[78]####
                searchAndExpand();//####[79]####
            }//####[80]####
            return _finalSchedule.get(0);//####[81]####
        } else {//####[82]####
            while (_finalSchedule.isEmpty() && (_openSchedules.size() < _numberOfCores)) //####[83]####
            {//####[83]####
                searchAndExpand();//####[84]####
            }//####[85]####
            if (_finalSchedule.isEmpty()) //####[86]####
            {//####[86]####
                TaskIDGroup g = paralleliseSearch(_openSchedules);//####[87]####
                try {//####[88]####
                    g.waitTillFinished();//####[89]####
                } catch (ExecutionException e) {//####[90]####
                    e.printStackTrace();//####[91]####
                } catch (InterruptedException e) {//####[92]####
                    e.printStackTrace();//####[93]####
                }//####[94]####
            }//####[95]####
            return _finalSchedule.get(0);//####[96]####
        }//####[97]####
    }//####[98]####
//####[99]####
    private static volatile Method __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = null;//####[99]####
    private synchronized static void __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet() {//####[99]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[99]####
            try {//####[99]####
                __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__paralleliseSearch", new Class[] {//####[99]####
                    PriorityBlockingQueue.class//####[99]####
                });//####[99]####
            } catch (Exception e) {//####[99]####
                e.printStackTrace();//####[99]####
            }//####[99]####
        }//####[99]####
    }//####[99]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[99]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[99]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[99]####
    }//####[99]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules, TaskInfo taskinfo) {//####[99]####
        // ensure Method variable is set//####[99]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[99]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[99]####
        }//####[99]####
        taskinfo.setParameters(_openSchedules);//####[99]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[99]####
        taskinfo.setInstance(this);//####[99]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[99]####
    }//####[99]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[99]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[99]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[99]####
    }//####[99]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[99]####
        // ensure Method variable is set//####[99]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[99]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[99]####
        }//####[99]####
        taskinfo.setTaskIdArgIndexes(0);//####[99]####
        taskinfo.addDependsOn(_openSchedules);//####[99]####
        taskinfo.setParameters(_openSchedules);//####[99]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[99]####
        taskinfo.setInstance(this);//####[99]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[99]####
    }//####[99]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[99]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[99]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[99]####
    }//####[99]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[99]####
        // ensure Method variable is set//####[99]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[99]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[99]####
        }//####[99]####
        taskinfo.setQueueArgIndexes(0);//####[99]####
        taskinfo.setIsPipeline(true);//####[99]####
        taskinfo.setParameters(_openSchedules);//####[99]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[99]####
        taskinfo.setInstance(this);//####[99]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[99]####
    }//####[99]####
    public void __pt__paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[99]####
        while (_finalSchedule.isEmpty()) //####[100]####
        {//####[100]####
            searchAndExpand();//####[101]####
        }//####[102]####
    }//####[103]####
//####[103]####
//####[104]####
    private void searchAndExpand() {//####[104]####
        Schedule currentSchedule = _openSchedules.poll();//####[105]####
        if (_visualisation) //####[106]####
        {//####[106]####
            fireScheduleChangeEvent(currentSchedule);//####[107]####
        }//####[108]####
        _closedSchedules.add(currentSchedule);//####[109]####
        if (this.hasScheduleUsedAllPossibleVertices(currentSchedule)) //####[110]####
        {//####[110]####
            _finalSchedule.add(currentSchedule);//####[111]####
        }//####[112]####
        this.addCurrentSchedulePossibleSuccessorsToOpenSchedule(currentSchedule);//####[113]####
    }//####[114]####
//####[124]####
    /**
	 * For the current schedule we are processing,
	 * it tries to find successor schedules that are available
	 * Those successors schedules are then added to the open schedule if it
	 * passes the conditions required 
	 *
	 * @param currentSchedule
	 *///####[124]####
    private void addCurrentSchedulePossibleSuccessorsToOpenSchedule(Schedule currentSchedule) {//####[124]####
        List<Vertex> currentVertexSuccessors = currentSchedule.getChildVertices();//####[125]####
        for (Vertex childVertex : currentVertexSuccessors) //####[126]####
        {//####[126]####
            Schedule currentScheduleCopy = new Schedule(currentSchedule);//####[128]####
            Schedule[] currentChildVertexSchedules = new Schedule[_numberOfProcessors];//####[129]####
            currentChildVertexSchedules = currentScheduleCopy.generateAllPossibleScheduleForSpecifiedVertex(childVertex);//####[131]####
            CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();//####[133]####
            int parentScheduleCost = costFunctionCalculator.getTotalCostFunction(currentSchedule);//####[134]####
            for (int i = 0; i < _numberOfProcessors; i++) //####[136]####
            {//####[136]####
                int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[137]####
                if (childScheduleCost <= _upperBoundCost) //####[139]####
                {//####[139]####
                    if (parentScheduleCost == costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i])) //####[141]####
                    {//####[141]####
                        _openSchedules.add(currentChildVertexSchedules[i]);//####[142]####
                        break;//####[143]####
                    }//####[144]####
                    if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[145]####
                    {//####[145]####
                        _openSchedules.add(currentChildVertexSchedules[i]);//####[146]####
                    }//####[147]####
                }//####[148]####
            }//####[149]####
        }//####[150]####
    }//####[151]####
//####[164]####
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
	 *///####[164]####
    private boolean checkScheduleThroughPruning(Schedule childSchedule) {//####[164]####
        Pruning pruning = new Pruning();//####[165]####
        if (pruning.isCurrentScheduleNeeded(_openSchedules, _closedSchedules, childSchedule)) //####[166]####
        {//####[166]####
            return true;//####[167]####
        }//####[168]####
        return false;//####[169]####
    }//####[171]####
//####[181]####
    /**
	 * This method checks if the current schedule is a finished schedule
	 *
	 * returns true if it is
	 * otherwise returns false
	 *
	 * @param currentSchedule
	 * @return
	 *///####[181]####
    private boolean hasScheduleUsedAllPossibleVertices(Schedule currentSchedule) {//####[181]####
        List<Vertex> currentScheduleUsedVertices = currentSchedule.getAllUsedVertices();//####[182]####
        for (Vertex vertex : Graph.getInstance().getVertices()) //####[183]####
        {//####[183]####
            if (!currentScheduleUsedVertices.contains(vertex)) //####[184]####
            {//####[184]####
                return false;//####[185]####
            }//####[186]####
        }//####[187]####
        return true;//####[188]####
    }//####[189]####
//####[198]####
    /**
	 * This method adds root schedules to openschedules
	 * since at the start of schedule the first task is the same no matter which processor
	 * it is put on so only one variation of root schedule is added.
	 *
	 *///####[198]####
    private void addRootVerticesSchedulesToOpenSchedule() {//####[198]####
        for (Vertex rootVertex : Graph.getInstance().getRootVertices()) //####[199]####
        {//####[199]####
            Schedule emptySchedule = new Schedule(_numberOfProcessors);//####[200]####
            Schedule[] rootSchedules = new Schedule[_numberOfProcessors];//####[201]####
            rootSchedules = emptySchedule.generateAllPossibleScheduleForSpecifiedVertex(rootVertex);//####[203]####
            _openSchedules.add(rootSchedules[0]);//####[205]####
        }//####[206]####
    }//####[207]####
//####[208]####
    private void fireScheduleChangeEvent(Schedule currentSchedule) {//####[208]####
        for (ScheduleListener listener : _listeners) //####[209]####
        {//####[209]####
            listener.update(currentSchedule);//####[210]####
        }//####[211]####
    }//####[212]####
}//####[212]####
