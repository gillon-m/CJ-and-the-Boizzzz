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
import data.Data;//####[21]####
//####[21]####
//-- ParaTask related imports//####[21]####
import pt.runtime.*;//####[21]####
import java.util.concurrent.ExecutionException;//####[21]####
import java.util.concurrent.locks.*;//####[21]####
import java.lang.reflect.*;//####[21]####
import pt.runtime.GuiThread;//####[21]####
import java.util.concurrent.BlockingQueue;//####[21]####
import java.util.ArrayList;//####[21]####
import java.util.List;//####[21]####
//####[21]####
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
    private VisualiserController _visualiserController;//####[46]####
//####[47]####
    private StopWatch _stopWatch;//####[47]####
//####[48]####
    private Schedule _bestSchedule;//####[48]####
//####[49]####
    private Data _data;//####[49]####
//####[51]####
    public ParallelisedScheduler(int numberOfProcessors, int numberOfCores, boolean visualisation) {//####[51]####
        _openSchedules = new PriorityBlockingQueue<Schedule>(Graph.getInstance().getVertices().size(), new ScheduleComparator());//####[52]####
        _closedSchedules = new ConcurrentLinkedQueue<Schedule>();//####[53]####
        _partialExpanded = new ArrayList<Schedule>();//####[54]####
        _finalSchedule = new ArrayList<Schedule>();//####[55]####
        _numberOfProcessors = numberOfProcessors;//####[56]####
        _numberOfCores = numberOfCores;//####[57]####
        ListScheduling ls = new ListScheduling(_numberOfProcessors);//####[58]####
        _upperBoundCost = ls.getUpperBoundCostFunction();//####[59]####
        _visualisation = visualisation;//####[60]####
        _stopWatch = StopWatch.getInstance();//####[61]####
        _data = Data.getInstance();//####[62]####
        if (_visualisation) //####[63]####
        {//####[63]####
            _visualiserController = new VisualiserController();//####[64]####
            setUpTimer();//####[65]####
        }//####[66]####
    }//####[67]####
//####[68]####
    private void setUpTimer() {//####[68]####
        action = new ActionListener() {//####[68]####
//####[70]####
            public void actionPerformed(ActionEvent arg0) {//####[70]####
                if (timerCount == 0) //####[71]####
                {//####[71]####
                    Schedule schedule = _openSchedules.peek();//####[72]####
                    _data.setCurrentSchedule(schedule);//####[73]####
                    _data.updateTotalNumberOfCreatedSchedules(_openSchedules.size() + _closedSchedules.size());//####[74]####
                    _visualiserController.update(false);//####[75]####
                } else {//####[76]####
                    timerCount--;//####[77]####
                }//####[78]####
            }//####[79]####
        };//####[79]####
        _timer = new Timer(1, action);//####[83]####
        _timer.setInitialDelay(0);//####[84]####
        _timer.start();//####[85]####
    }//####[86]####
//####[93]####
    /**
	 * This method returns the optimal schedule
	 * @return void
	 * @throws Exception
	 *///####[93]####
    public Schedule getOptimalSchedule() {//####[93]####
        this.addRootVerticesSchedulesToOpenSchedule();//####[94]####
        _stopWatch.start();//####[95]####
        Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();//####[96]####
        if (_visualisation) //####[97]####
        {//####[97]####
            _timer.stop();//####[98]####
            _data.setCurrentSchedule(optimalSchedule);//####[99]####
            _data.updateTotalNumberOfCreatedSchedules(_openSchedules.size() + _closedSchedules.size());//####[100]####
            _visualiserController.update(true);//####[101]####
        }//####[102]####
        _stopWatch.stop();//####[103]####
        return optimalSchedule;//####[104]####
    }//####[105]####
//####[114]####
    /**
	 * This method uses the A* algorithm to create schedules 
	 * It only returns back once it finds an optimal schedule
	 * It throws an exception if the openschedule queue is empty because that is not suppose to happen
	 * 
	 * @return optimal schedule
	 *///####[114]####
    private Schedule makeSchedulesUsingAlgorithm() {//####[114]####
        if (_numberOfCores == -1) //####[115]####
        {//####[115]####
            while (_finalSchedule.isEmpty()) //####[116]####
            {//####[116]####
                searchAndExpand();//####[117]####
            }//####[118]####
            return _finalSchedule.get(0);//####[119]####
        } else {//####[120]####
            while (_finalSchedule.isEmpty() && (_openSchedules.size() < _numberOfCores)) //####[121]####
            {//####[121]####
                searchAndExpand();//####[122]####
            }//####[123]####
            if (_finalSchedule.isEmpty()) //####[124]####
            {//####[124]####
                TaskIDGroup g = paralleliseSearch(_openSchedules);//####[125]####
                try {//####[126]####
                    g.waitTillFinished();//####[127]####
                } catch (ExecutionException e) {//####[128]####
                    e.printStackTrace();//####[129]####
                } catch (InterruptedException e) {//####[130]####
                    e.printStackTrace();//####[131]####
                }//####[132]####
            }//####[133]####
            return _finalSchedule.get(0);//####[134]####
        }//####[135]####
    }//####[136]####
//####[137]####
    private void searchAndExpand() {//####[137]####
        Schedule currentSchedule = _openSchedules.poll();//####[138]####
        _closedSchedules.add(currentSchedule);//####[139]####
        if (this.hasScheduleUsedAllPossibleVertices(currentSchedule)) //####[140]####
        {//####[140]####
            _finalSchedule.add(currentSchedule);//####[141]####
        }//####[142]####
        this.addCurrentSchedulePossibleSuccessorsToOpenSchedule(currentSchedule);//####[143]####
    }//####[144]####
//####[145]####
    private static volatile Method __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = null;//####[145]####
    private synchronized static void __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet() {//####[145]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[145]####
            try {//####[145]####
                __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__paralleliseSearch", new Class[] {//####[145]####
                    PriorityBlockingQueue.class//####[145]####
                });//####[145]####
            } catch (Exception e) {//####[145]####
                e.printStackTrace();//####[145]####
            }//####[145]####
        }//####[145]####
    }//####[145]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[145]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[145]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[145]####
    }//####[145]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules, TaskInfo taskinfo) {//####[145]####
        // ensure Method variable is set//####[145]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[145]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[145]####
        }//####[145]####
        taskinfo.setParameters(_openSchedules);//####[145]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[145]####
        taskinfo.setInstance(this);//####[145]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[145]####
    }//####[145]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[145]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[145]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[145]####
    }//####[145]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[145]####
        // ensure Method variable is set//####[145]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[145]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[145]####
        }//####[145]####
        taskinfo.setTaskIdArgIndexes(0);//####[145]####
        taskinfo.addDependsOn(_openSchedules);//####[145]####
        taskinfo.setParameters(_openSchedules);//####[145]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[145]####
        taskinfo.setInstance(this);//####[145]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[145]####
    }//####[145]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[145]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[145]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[145]####
    }//####[145]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[145]####
        // ensure Method variable is set//####[145]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[145]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[145]####
        }//####[145]####
        taskinfo.setQueueArgIndexes(0);//####[145]####
        taskinfo.setIsPipeline(true);//####[145]####
        taskinfo.setParameters(_openSchedules);//####[145]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[145]####
        taskinfo.setInstance(this);//####[145]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[145]####
    }//####[145]####
    public void __pt__paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[145]####
        while (_finalSchedule.isEmpty()) //####[146]####
        {//####[146]####
            searchAndExpand();//####[147]####
        }//####[148]####
    }//####[149]####
//####[149]####
//####[159]####
    /**
	 * For the current schedule we are processing,
	 * it tries to find successor schedules that are available
	 * Those successors schedules are then added to the open schedule if it
	 * passes the conditions required 
	 *
	 * @param currentSchedule
	 *///####[159]####
    private void addCurrentSchedulePossibleSuccessorsToOpenSchedule(Schedule currentSchedule) {//####[159]####
        List<Vertex> currentVertexSuccessors = currentSchedule.getChildVertices();//####[160]####
        for (Vertex childVertex : currentVertexSuccessors) //####[161]####
        {//####[161]####
            Schedule currentScheduleCopy = new Schedule(currentSchedule);//####[163]####
            Schedule[] currentChildVertexSchedules = new Schedule[_numberOfProcessors];//####[164]####
            currentChildVertexSchedules = currentScheduleCopy.generateAllPossibleScheduleForSpecifiedVertex(childVertex);//####[166]####
            CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();//####[168]####
            int parentScheduleCost = costFunctionCalculator.getTotalCostFunction(currentSchedule);//####[169]####
            if (_partialExpanded.contains(currentSchedule)) //####[171]####
            {//####[171]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[172]####
                {//####[172]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[173]####
                    if (childScheduleCost <= _upperBoundCost) //####[175]####
                    {//####[175]####
                        if (parentScheduleCost > costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i])) //####[176]####
                        {//####[176]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[177]####
                            {//####[177]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[178]####
                            }//####[179]####
                        }//####[180]####
                    }//####[181]####
                }//####[182]####
            } else {//####[183]####
                boolean partialExpanded = false;//####[184]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[185]####
                {//####[185]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[186]####
                    if (parentScheduleCost >= childScheduleCost) //####[187]####
                    {//####[187]####
                        if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[188]####
                        {//####[188]####
                            _openSchedules.add(currentChildVertexSchedules[i]);//####[189]####
                            partialExpanded = true;//####[190]####
                        }//####[191]####
                    }//####[192]####
                }//####[193]####
                if (!partialExpanded) //####[194]####
                {//####[194]####
                    for (int i = 0; i < _numberOfProcessors; i++) //####[195]####
                    {//####[195]####
                        int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[196]####
                        if (childScheduleCost <= _upperBoundCost) //####[198]####
                        {//####[198]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[199]####
                            {//####[199]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[200]####
                            }//####[201]####
                        }//####[202]####
                    }//####[203]####
                } else {//####[204]####
                    _openSchedules.add(currentSchedule);//####[205]####
                    _closedSchedules.remove(currentSchedule);//####[206]####
                    _partialExpanded.add(currentSchedule);//####[207]####
                }//####[208]####
            }//####[209]####
        }//####[210]####
    }//####[211]####
//####[225]####
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
	 *///####[225]####
    private boolean checkScheduleThroughPruning(Schedule childSchedule) {//####[225]####
        Pruning pruning = new Pruning();//####[226]####
        if (pruning.isCurrentScheduleNeeded(_openSchedules, _closedSchedules, childSchedule)) //####[227]####
        {//####[227]####
            return true;//####[228]####
        }//####[229]####
        return false;//####[230]####
    }//####[232]####
//####[242]####
    /**
	 * This method checks if the current schedule is a finished schedule
	 *
	 * returns true if it is
	 * otherwise returns false
	 *
	 * @param currentSchedule
	 * @return
	 *///####[242]####
    private boolean hasScheduleUsedAllPossibleVertices(Schedule currentSchedule) {//####[242]####
        List<Vertex> currentScheduleUsedVertices = currentSchedule.getAllUsedVertices();//####[243]####
        for (Vertex vertex : Graph.getInstance().getVertices()) //####[244]####
        {//####[244]####
            if (!currentScheduleUsedVertices.contains(vertex)) //####[245]####
            {//####[245]####
                return false;//####[246]####
            }//####[247]####
        }//####[248]####
        return true;//####[249]####
    }//####[250]####
//####[259]####
    /**
	 * This method adds root schedules to openschedules
	 * since at the start of schedule the first _timerTask is the same no matter which processor
	 * it is put on so only one variation of root schedule is added.
	 *
	 *///####[259]####
    private void addRootVerticesSchedulesToOpenSchedule() {//####[259]####
        for (Vertex rootVertex : Graph.getInstance().getRootVertices()) //####[260]####
        {//####[260]####
            Schedule emptySchedule = new Schedule(_numberOfProcessors);//####[261]####
            Schedule[] rootSchedules = new Schedule[_numberOfProcessors];//####[262]####
            rootSchedules = emptySchedule.generateAllPossibleScheduleForSpecifiedVertex(rootVertex);//####[264]####
            _openSchedules.add(rootSchedules[0]);//####[266]####
        }//####[267]####
    }//####[268]####
}//####[268]####
