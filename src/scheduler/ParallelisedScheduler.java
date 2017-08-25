package scheduler;//####[1]####
//####[1]####
import java.awt.event.ActionEvent;//####[3]####
import java.awt.event.ActionListener;//####[4]####
import java.util.ArrayList;//####[5]####
import java.util.List;//####[6]####
import java.util.concurrent.ConcurrentLinkedQueue;//####[7]####
import java.util.concurrent.PriorityBlockingQueue;//####[8]####
import javax.swing.Timer;//####[10]####
import graph.Graph;//####[12]####
import graph.Vertex;//####[13]####
import gui.ScheduleListener;//####[14]####
import gui.VisualiserController;//####[15]####
import heuristics.CostFunctionCalculator;//####[16]####
import pruning.ListScheduling;//####[17]####
import pruning.Pruning;//####[18]####
import components.ScheduleComparator;//####[19]####
import data.StopWatch;//####[20]####
//####[20]####
//-- ParaTask related imports//####[20]####
import pt.runtime.*;//####[20]####
import java.util.concurrent.ExecutionException;//####[20]####
import java.util.concurrent.locks.*;//####[20]####
import java.lang.reflect.*;//####[20]####
import pt.runtime.GuiThread;//####[20]####
import java.util.concurrent.BlockingQueue;//####[20]####
import java.util.ArrayList;//####[20]####
import java.util.List;//####[20]####
//####[20]####
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
 *///####[33]####
public class ParallelisedScheduler {//####[34]####
    static{ParaTask.init();}//####[34]####
    /*  ParaTask helper method to access private/protected slots *///####[34]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[34]####
        if (m.getParameterTypes().length == 0)//####[34]####
            m.invoke(instance);//####[34]####
        else if ((m.getParameterTypes().length == 1))//####[34]####
            m.invoke(instance, arg);//####[34]####
        else //####[34]####
            m.invoke(instance, arg, interResult);//####[34]####
    }//####[34]####
//####[35]####
    private int _numberOfProcessors;//####[35]####
//####[36]####
    private PriorityBlockingQueue<Schedule> _openSchedules;//####[36]####
//####[37]####
    private ConcurrentLinkedQueue<Schedule> _closedSchedules;//####[37]####
//####[38]####
    private List<Schedule> _partialExpanded;//####[38]####
//####[39]####
    private List<Schedule> _finalSchedule;//####[39]####
//####[40]####
    private int _upperBoundCost;//####[40]####
//####[41]####
    private int _numberOfCores;//####[41]####
//####[42]####
    private boolean _visualisation;//####[42]####
//####[43]####
    private Timer _timer;//####[43]####
//####[44]####
    private ActionListener action;//####[44]####
//####[45]####
    private int timerCount;//####[45]####
//####[46]####
    private ScheduleListener _visualiserController;//####[46]####
//####[47]####
    private StopWatch _stopWatch;//####[47]####
//####[49]####
    public ParallelisedScheduler(int numberOfProcessors, int numberOfCores, boolean visualisation) {//####[49]####
        _openSchedules = new PriorityBlockingQueue<Schedule>(Graph.getInstance().getVertices().size(), new ScheduleComparator());//####[50]####
        _closedSchedules = new ConcurrentLinkedQueue<Schedule>();//####[51]####
        _partialExpanded = new ArrayList<Schedule>();//####[52]####
        _finalSchedule = new ArrayList<Schedule>();//####[53]####
        _numberOfProcessors = numberOfProcessors;//####[54]####
        _numberOfCores = numberOfCores;//####[55]####
        ListScheduling ls = new ListScheduling(_numberOfProcessors);//####[56]####
        _upperBoundCost = ls.getUpperBoundCostFunction();//####[57]####
        _visualisation = visualisation;//####[58]####
        _stopWatch = StopWatch.getInstance();//####[59]####
        if (_visualisation) //####[60]####
        {//####[60]####
            _visualiserController = new VisualiserController();//####[61]####
            setUpTimer();//####[62]####
        }//####[63]####
    }//####[64]####
//####[65]####
    private void setUpTimer() {//####[65]####
        action = new ActionListener() {//####[65]####
//####[67]####
            public void actionPerformed(ActionEvent arg0) {//####[67]####
                if (timerCount == 0) //####[68]####
                {//####[68]####
                    Schedule schedule = _openSchedules.peek();//####[69]####
                    _visualiserController.update(schedule, false, _openSchedules.size() + _closedSchedules.size());//####[70]####
                } else {//####[71]####
                    timerCount--;//####[72]####
                }//####[73]####
            }//####[74]####
        };//####[74]####
        _timer = new Timer(1, action);//####[76]####
        _timer.setInitialDelay(0);//####[77]####
        _timer.start();//####[78]####
    }//####[79]####
//####[86]####
    /**
	 * This method returns the optimal schedule
	 * @return void
	 * @throws Exception
	 *///####[86]####
    public Schedule getOptimalSchedule() {//####[86]####
        this.addRootVerticesSchedulesToOpenSchedule();//####[87]####
        _stopWatch.start();//####[88]####
        Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();//####[89]####
        if (_visualisation) //####[90]####
        {//####[90]####
            _timer.stop();//####[91]####
            _visualiserController.update(optimalSchedule, true, _openSchedules.size() + _closedSchedules.size());//####[92]####
        }//####[93]####
        _stopWatch.stop();//####[94]####
        return optimalSchedule;//####[95]####
    }//####[96]####
//####[105]####
    /**
	 * This method uses the A* algorithm to create schedules 
	 * It only returns back once it finds an optimal schedule
	 * It throws an exception if the openschedule queue is empty because that is not suppose to happen
	 * 
	 * @return optimal schedule
	 *///####[105]####
    private Schedule makeSchedulesUsingAlgorithm() {//####[105]####
        if (_numberOfCores == -1) //####[106]####
        {//####[106]####
            while (_finalSchedule.isEmpty()) //####[107]####
            {//####[107]####
                searchAndExpand();//####[108]####
            }//####[109]####
            return _finalSchedule.get(0);//####[110]####
        } else {//####[111]####
            while (_finalSchedule.isEmpty() && (_openSchedules.size() < _numberOfCores)) //####[112]####
            {//####[112]####
                searchAndExpand();//####[113]####
            }//####[114]####
            if (_finalSchedule.isEmpty()) //####[115]####
            {//####[115]####
                TaskIDGroup g = paralleliseSearch(_openSchedules);//####[116]####
                try {//####[117]####
                    g.waitTillFinished();//####[118]####
                } catch (ExecutionException e) {//####[119]####
                    e.printStackTrace();//####[120]####
                } catch (InterruptedException e) {//####[121]####
                    e.printStackTrace();//####[122]####
                }//####[123]####
            }//####[124]####
            return _finalSchedule.get(0);//####[125]####
        }//####[126]####
    }//####[127]####
//####[128]####
    private void searchAndExpand() {//####[128]####
        Schedule currentSchedule = _openSchedules.poll();//####[129]####
        _closedSchedules.add(currentSchedule);//####[130]####
        if (this.hasScheduleUsedAllPossibleVertices(currentSchedule)) //####[131]####
        {//####[131]####
            _finalSchedule.add(currentSchedule);//####[132]####
        }//####[133]####
        this.addCurrentSchedulePossibleSuccessorsToOpenSchedule(currentSchedule);//####[134]####
    }//####[135]####
//####[136]####
    private static volatile Method __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = null;//####[136]####
    private synchronized static void __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet() {//####[136]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[136]####
            try {//####[136]####
                __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__paralleliseSearch", new Class[] {//####[136]####
                    PriorityBlockingQueue.class//####[136]####
                });//####[136]####
            } catch (Exception e) {//####[136]####
                e.printStackTrace();//####[136]####
            }//####[136]####
        }//####[136]####
    }//####[136]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[136]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[136]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[136]####
    }//####[136]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules, TaskInfo taskinfo) {//####[136]####
        // ensure Method variable is set//####[136]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[136]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[136]####
        }//####[136]####
        taskinfo.setParameters(_openSchedules);//####[136]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[136]####
        taskinfo.setInstance(this);//####[136]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[136]####
    }//####[136]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[136]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[136]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[136]####
    }//####[136]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[136]####
        // ensure Method variable is set//####[136]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[136]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[136]####
        }//####[136]####
        taskinfo.setTaskIdArgIndexes(0);//####[136]####
        taskinfo.addDependsOn(_openSchedules);//####[136]####
        taskinfo.setParameters(_openSchedules);//####[136]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[136]####
        taskinfo.setInstance(this);//####[136]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[136]####
    }//####[136]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[136]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[136]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[136]####
    }//####[136]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[136]####
        // ensure Method variable is set//####[136]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[136]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[136]####
        }//####[136]####
        taskinfo.setQueueArgIndexes(0);//####[136]####
        taskinfo.setIsPipeline(true);//####[136]####
        taskinfo.setParameters(_openSchedules);//####[136]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[136]####
        taskinfo.setInstance(this);//####[136]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[136]####
    }//####[136]####
    public void __pt__paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[136]####
        while (_finalSchedule.isEmpty()) //####[137]####
        {//####[137]####
            searchAndExpand();//####[138]####
        }//####[139]####
    }//####[140]####
//####[140]####
//####[150]####
    /**
	 * For the current schedule we are processing,
	 * it tries to find successor schedules that are available
	 * Those successors schedules are then added to the open schedule if it
	 * passes the conditions required 
	 *
	 * @param currentSchedule
	 *///####[150]####
    private void addCurrentSchedulePossibleSuccessorsToOpenSchedule(Schedule currentSchedule) {//####[150]####
        List<Vertex> currentVertexSuccessors = currentSchedule.getChildVertices();//####[151]####
        for (Vertex childVertex : currentVertexSuccessors) //####[152]####
        {//####[152]####
            Schedule currentScheduleCopy = new Schedule(currentSchedule);//####[154]####
            Schedule[] currentChildVertexSchedules = new Schedule[_numberOfProcessors];//####[155]####
            currentChildVertexSchedules = currentScheduleCopy.generateAllPossibleScheduleForSpecifiedVertex(childVertex);//####[157]####
            CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();//####[159]####
            int parentScheduleCost = costFunctionCalculator.getTotalCostFunction(currentSchedule);//####[160]####
            if (_partialExpanded.contains(currentSchedule)) //####[162]####
            {//####[162]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[163]####
                {//####[163]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[164]####
                    if (childScheduleCost <= _upperBoundCost) //####[166]####
                    {//####[166]####
                        if (parentScheduleCost > costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i])) //####[167]####
                        {//####[167]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[168]####
                            {//####[168]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[169]####
                            }//####[170]####
                        }//####[171]####
                    }//####[172]####
                }//####[173]####
            } else {//####[174]####
                boolean partialExpanded = false;//####[175]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[176]####
                {//####[176]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[177]####
                    if (parentScheduleCost >= childScheduleCost) //####[178]####
                    {//####[178]####
                        if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[179]####
                        {//####[179]####
                            _openSchedules.add(currentChildVertexSchedules[i]);//####[180]####
                            partialExpanded = true;//####[181]####
                        }//####[182]####
                    }//####[183]####
                }//####[184]####
                if (!partialExpanded) //####[185]####
                {//####[185]####
                    for (int i = 0; i < _numberOfProcessors; i++) //####[186]####
                    {//####[186]####
                        int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[187]####
                        if (childScheduleCost <= _upperBoundCost) //####[189]####
                        {//####[189]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[190]####
                            {//####[190]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[191]####
                            }//####[192]####
                        }//####[193]####
                    }//####[194]####
                } else {//####[195]####
                    _openSchedules.add(currentSchedule);//####[196]####
                    _closedSchedules.remove(currentSchedule);//####[197]####
                    _partialExpanded.add(currentSchedule);//####[198]####
                }//####[199]####
            }//####[200]####
        }//####[201]####
    }//####[202]####
//####[216]####
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
	 *///####[216]####
    private boolean checkScheduleThroughPruning(Schedule childSchedule) {//####[216]####
        Pruning pruning = new Pruning();//####[217]####
        if (pruning.isCurrentScheduleNeeded(_openSchedules, _closedSchedules, childSchedule)) //####[218]####
        {//####[218]####
            return true;//####[219]####
        }//####[220]####
        return false;//####[221]####
    }//####[223]####
//####[233]####
    /**
	 * This method checks if the current schedule is a finished schedule
	 *
	 * returns true if it is
	 * otherwise returns false
	 *
	 * @param currentSchedule
	 * @return
	 *///####[233]####
    private boolean hasScheduleUsedAllPossibleVertices(Schedule currentSchedule) {//####[233]####
        List<Vertex> currentScheduleUsedVertices = currentSchedule.getAllUsedVertices();//####[234]####
        for (Vertex vertex : Graph.getInstance().getVertices()) //####[235]####
        {//####[235]####
            if (!currentScheduleUsedVertices.contains(vertex)) //####[236]####
            {//####[236]####
                return false;//####[237]####
            }//####[238]####
        }//####[239]####
        return true;//####[240]####
    }//####[241]####
//####[250]####
    /**
	 * This method adds root schedules to openschedules
	 * since at the start of schedule the first _timerTask is the same no matter which processor
	 * it is put on so only one variation of root schedule is added.
	 *
	 *///####[250]####
    private void addRootVerticesSchedulesToOpenSchedule() {//####[250]####
        for (Vertex rootVertex : Graph.getInstance().getRootVertices()) //####[251]####
        {//####[251]####
            Schedule emptySchedule = new Schedule(_numberOfProcessors);//####[252]####
            Schedule[] rootSchedules = new Schedule[_numberOfProcessors];//####[253]####
            rootSchedules = emptySchedule.generateAllPossibleScheduleForSpecifiedVertex(rootVertex);//####[255]####
            _openSchedules.add(rootSchedules[0]);//####[257]####
        }//####[258]####
    }//####[259]####
}//####[259]####
